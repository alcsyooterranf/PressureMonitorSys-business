package org.pms.infrastructure.adapter.repository.clickhouse;

import lombok.extern.slf4j.Slf4j;
import org.pms.infrastructure.mapper.po.DeviceDataPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ClickHouse监控数据Repository
 * 负责将监控数据写入ClickHouse,以及查询历史数据
 * <p>
 * ClickHouse特点:
 * 1. 列式存储,查询速度快(特别是聚合查询)
 * 2. 压缩率高,存储成本低
 * 3. 不支持UPDATE/DELETE(或性能很差),只适合INSERT和SELECT
 * 4. 批量写入性能极高,单条写入性能一般
 *
 * @author zeal
 * @since 2024-11-24
 */
@Slf4j
@Repository
public class ClickHouseDataReportRepository {
	
	/**
	 * ClickHouse专用的JdbcTemplate
	 *
	 * @Qualifier注解指定使用哪个Bean(因为有多个JdbcTemplate)
	 */
	@Autowired
	@Qualifier("clickHouseJdbcTemplate")
	private JdbcTemplate clickHouseJdbcTemplate;
	
	/**
	 * 批量插入监控数据到ClickHouse
	 * <p>
	 * 为什么要批量插入?
	 * - ClickHouse对批量写入做了优化,批量写入比单条写入快100倍以上
	 * - 建议每次写入1000-10000条数据
	 *
	 * @param dataList 监控数据列表
	 */
	public void batchInsertMonitorData(List<DeviceDataPO> dataList) {
		if (dataList == null || dataList.isEmpty()) {
			return;
		}
		
		// SQL插入语句
		// 注意:ClickHouse不需要指定id(会自动生成),但为了和MySQL保持一致,这里还是插入
		// 新增了process_state, is_removed等字段
		String sql = """
				INSERT INTO pressuremonitorsys.t_monitor_data_report
				(id, up_packet_sn, up_data_sn, topic, tenant_id, service_id, protocol,
				 pipeline_id, pipeline_sn, temperature, voltage, pressure, abnormal_flag,
				 message_type, device_type, device_id, device_sn, assoc_asset_id,
				 imsi, imei, create_time, process_state, is_removed)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";
		
		try {
			// 使用JdbcTemplate的batchUpdate方法批量插入
			// BatchPreparedStatementSetter是一个回调接口,用于设置每条记录的参数
			clickHouseJdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				
				/**
				 * 设置每条记录的参数
				 * @param ps PreparedStatement对象
				 * @param i 当前记录的索引(从0开始)
				 */
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					DeviceDataPO data = dataList.get(i);
					
					// 按照SQL中的?顺序设置参数
					ps.setLong(1, data.getId());
					ps.setString(5, data.getTenantId());
					ps.setString(7, data.getProtocol());
					ps.setLong(8, data.getPipelineId());
					ps.setString(9, data.getPipelineSN());
					ps.setInt(10, data.getTemperature());
					ps.setInt(11, data.getVoltage());
					ps.setInt(12, data.getPressure());
					ps.setString(13, data.getAbnormalFlag());
					ps.setString(15, data.getDeviceType());
					ps.setLong(16, data.getDeviceId());
					ps.setString(17, data.getDeviceSN());
					ps.setString(18, data.getAssocAssetId());
					ps.setString(19, data.getIMSI());
					ps.setString(20, data.getIMEI());
					// Date转Timestamp(ClickHouse的DateTime类型)
					ps.setTimestamp(21, new Timestamp(data.getCreateTime().getTime()));
					
					// 新增字段: 处理状态和删除标志
					// Boolean转UInt8: true=1, false=0
					ps.setInt(22, (data.getProcessState() != null && data.getProcessState()) ? 1 : 0);
					ps.setInt(23, (data.getRemoved() != null && data.getRemoved()) ? 1 : 0);
				}
				
				/**
				 * 返回批量插入的记录数
				 */
				@Override
				public int getBatchSize() {
					return dataList.size();
				}
			});
			
			log.info("ClickHouse批量写入成功,数量: {}", dataList.size());
		} catch (Exception e) {
			log.error("ClickHouse批量写入失败,数量: {}", dataList.size(), e);
			throw new RuntimeException("ClickHouse写入失败", e);
		}
	}
	
	/**
	 * 查询单个设备的时间范围数据
	 * <p>
	 * 使用场景:查看某个设备在某个时间段的监控数据
	 *
	 * @param deviceSN  设备序列号
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @return 监控数据列表
	 */
	public List<DeviceDataPO> queryByTimeRange(String deviceSN, Date startTime, Date endTime) {
		// ClickHouse的SQL语法和MySQL基本一致
		// 但性能要快得多,因为:
		// 1. 数据按(device_sn, create_time)排序,查询时可以快速定位
		// 2. 列式存储,只读取需要的列
		String sql = """
				SELECT * FROM pressuremonitorsys.t_monitor_data_report
				WHERE device_sn = ?
				  AND create_time >= ?
				  AND create_time <= ?
				ORDER BY create_time DESC
				LIMIT 10000
				""";
		
		try {
			// BeanPropertyRowMapper会自动将查询结果映射到DataReportPO对象
			// 它会根据列名和属性名自动匹配(支持下划线转驼峰)
			return clickHouseJdbcTemplate.query(sql,
					new BeanPropertyRowMapper<>(DeviceDataPO.class),
					deviceSN, startTime, endTime);
		} catch (Exception e) {
			log.error("ClickHouse查询失败: deviceSN={}, startTime={}, endTime={}",
					deviceSN, startTime, endTime, e);
			throw new RuntimeException("ClickHouse查询失败", e);
		}
	}
	
	/**
	 * 查询多个设备的时间范围数据
	 *
	 * @param deviceSNList 设备序列号列表
	 * @param startTime    开始时间
	 * @param endTime      结束时间
	 * @return 监控数据列表
	 */
	public List<DeviceDataPO> queryByTimeRangeMultiDevice(List<String> deviceSNList,
														  Date startTime, Date endTime) {
		if (deviceSNList == null || deviceSNList.isEmpty()) {
			return List.of();
		}
		
		// 构建IN子句的占位符: (?, ?, ?)
		String inClause = String.join(",", deviceSNList.stream()
				.map(sn -> "?")
				.toList());
		
		String sql = String.format("""
				SELECT * FROM pressuremonitorsys.t_monitor_data_report
				WHERE device_sn IN (%s)
				  AND create_time >= ?
				  AND create_time <= ?
				ORDER BY device_sn, create_time DESC
				LIMIT 10000
				""", inClause);
		
		try {
			// 构建参数数组: [deviceSN1, deviceSN2, ..., startTime, endTime]
			Object[] params = new Object[deviceSNList.size() + 2];
			for (int i = 0; i < deviceSNList.size(); i++) {
				params[i] = deviceSNList.get(i);
			}
			params[deviceSNList.size()] = startTime;
			params[deviceSNList.size() + 1] = endTime;
			
			return clickHouseJdbcTemplate.query(sql,
					new BeanPropertyRowMapper<>(DeviceDataPO.class),
					params);
		} catch (Exception e) {
			log.error("ClickHouse多设备查询失败: deviceSNList={}, startTime={}, endTime={}",
					deviceSNList, startTime, endTime, e);
			throw new RuntimeException("ClickHouse查询失败", e);
		}
	}
	
	/**
	 * 获取设备的统计数据
	 * <p>
	 * 使用场景:统计某个设备在某个时间段的平均温度、最大压力等
	 * ClickHouse的聚合查询性能极高,即使是亿级数据也能秒级返回
	 *
	 * @param deviceSN  设备序列号
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @return 统计数据Map
	 */
	public Map<String, Object> getDeviceStatistics(String deviceSN, Date startTime, Date endTime) {
		// ClickHouse的聚合函数:
		// - count(): 计数
		// - avg(): 平均值
		// - max(): 最大值
		// - min(): 最小值
		// - countIf(): 条件计数
		String sql = """
				SELECT
				    count() as total_count,
				    avg(temperature) as avg_temperature,
				    max(temperature) as max_temperature,
				    min(temperature) as min_temperature,
				    avg(voltage) as avg_voltage,
				    max(voltage) as max_voltage,
				    min(voltage) as min_voltage,
				    avg(pressure) as avg_pressure,
				    max(pressure) as max_pressure,
				    min(pressure) as min_pressure,
				    countIf(abnormal_flag > 0) as abnormal_count
				FROM pressuremonitorsys.t_monitor_data_report
				WHERE device_sn = ?
				  AND create_time >= ?
				  AND create_time <= ?
				""";
		
		try {
			// queryForMap返回单行结果的Map
			// key是列名,value是列值
			return clickHouseJdbcTemplate.queryForMap(sql, deviceSN, startTime, endTime);
		} catch (Exception e) {
			log.error("ClickHouse统计查询失败: deviceSN={}, startTime={}, endTime={}",
					deviceSN, startTime, endTime, e);
			throw new RuntimeException("ClickHouse统计查询失败", e);
		}
	}
	
	/**
	 * 按天统计异常数据趋势
	 * <p>
	 * 使用场景:生成异常数据趋势图表
	 *
	 * @param deviceSN  设备序列号
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @return 每天的统计数据列表
	 */
	public List<Map<String, Object>> getAbnormalTrend(String deviceSN, Date startTime, Date endTime) {
		// toDate()函数将DateTime转换为Date(只保留日期部分)
		// 位运算: abnormal_flag & 1 表示取最低位(压力超上限)
		String sql = """
				SELECT
				    toDate(create_time) as date,
				    count() as abnormal_count,
				    countIf(abnormal_flag & 1 > 0) as pressure_high_count,
				    countIf(abnormal_flag & 2 > 0) as pressure_low_count,
				    countIf(abnormal_flag & 4 > 0) as temperature_high_count,
				    countIf(abnormal_flag & 8 > 0) as temperature_low_count
				FROM pressuremonitorsys.t_monitor_data_report
				WHERE device_sn = ?
				  AND abnormal_flag > 0
				  AND create_time >= ?
				  AND create_time <= ?
				GROUP BY date
				ORDER BY date
				""";
		
		try {
			// queryForList返回多行结果的List<Map>
			return clickHouseJdbcTemplate.queryForList(sql, deviceSN, startTime, endTime);
		} catch (Exception e) {
			log.error("ClickHouse趋势查询失败: deviceSN={}, startTime={}, endTime={}",
					deviceSN, startTime, endTime, e);
			throw new RuntimeException("ClickHouse趋势查询失败", e);
		}
	}
	
	/**
	 * 查询所有设备的数据(支持条件过滤)
	 * <p>
	 * 使用场景:前端查询历史数据列表(不指定设备)
	 * 注意:为了防止查询数据量过大,限制返回10000条
	 *
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @return 数据列表
	 */
	public List<DeviceDataPO> queryAll(Date startTime, Date endTime) {
		String sql = """
				SELECT
				    id, up_packet_sn, up_data_sn, device_sn, device_id, pipeline_id,
				    temperature, voltage, pressure, abnormal_flag, create_time
				FROM pressuremonitorsys.t_monitor_data_report
				WHERE create_time >= ?
				  AND create_time <= ?
				ORDER BY create_time DESC
				LIMIT 10000
				""";
		
		try {
			return clickHouseJdbcTemplate.query(sql, this::mapRowToDataReportPO, startTime, endTime);
		} catch (Exception e) {
			log.error("ClickHouse全量查询失败: startTime={}, endTime={}", startTime, endTime, e);
			throw new RuntimeException("ClickHouse全量查询失败", e);
		}
	}
	
	/**
	 * 查询异常数据(只返回有异常的记录)
	 * <p>
	 * 使用场景:异常报告列表页面
	 *
	 * @param deviceSNList 设备序列号列表(可选,null表示查询所有设备)
	 * @param startTime    开始时间
	 * @param endTime      结束时间
	 * @return 异常数据列表
	 */
	public List<DeviceDataPO> queryAbnormalData(List<String> deviceSNList, Date startTime, Date endTime) {
		// 动态构建SQL
		StringBuilder sql = new StringBuilder("""
				SELECT
				    id, up_packet_sn, up_data_sn, device_sn, device_id, pipeline_id,
				    temperature, voltage, pressure, abnormal_flag, create_time,
				    process_state, process_time, process_by, is_removed
				FROM pressuremonitorsys.t_monitor_data_report
				WHERE abnormal_flag > 0
				  AND create_time >= ?
				  AND create_time <= ?
				""");
		
		List<Object> params = new ArrayList<>();
		params.add(startTime);
		params.add(endTime);
		
		// 如果指定了设备列表,添加IN条件
		if (deviceSNList != null && !deviceSNList.isEmpty()) {
			sql.append(" AND device_sn IN (");
			for (int i = 0; i < deviceSNList.size(); i++) {
				sql.append(i == 0 ? "?" : ",?");
				params.add(deviceSNList.get(i));
			}
			sql.append(")");
		}
		
		sql.append(" ORDER BY create_time DESC LIMIT 10000");
		
		try {
			return clickHouseJdbcTemplate.query(sql.toString(), this::mapRowToDataReportPO, params.toArray());
		} catch (Exception e) {
			log.error("ClickHouse异常数据查询失败: deviceSNList={}, startTime={}, endTime={}",
					deviceSNList, startTime, endTime, e);
			throw new RuntimeException("ClickHouse异常数据查询失败", e);
		}
	}
	
	/**
	 * 按条件查询异常数据(支持处理状态过滤)
	 * <p>
	 * 使用场景:前端异常报告列表,支持按处理状态筛选
	 * <p>
	 * ClickHouse性能分析:
	 * 1. process_state字段有索引(TYPE set),查询性能极高
	 * 2. 单表查询,无需JOIN,性能优于MySQL 10-100倍
	 * 3. 即使数据量达到亿级,查询依然是毫秒级响应
	 *
	 * @param deviceSN     设备序列号(可选)
	 * @param processState 处理状态(可选,true=已处理,false=未处理,null=全部)
	 * @param startTime    开始时间
	 * @param endTime      结束时间
	 * @return 异常数据列表
	 */
	public List<DeviceDataPO> queryByConditions(String deviceSN, Boolean processState,
												Date startTime, Date endTime) {
		// 动态构建SQL
		// 注意: ClickHouse的WHERE条件顺序会影响性能
		// 建议顺序: 1.时间范围 2.设备SN 3.其他条件
		StringBuilder sql = new StringBuilder("""
				SELECT
				    id, up_packet_sn, up_data_sn, device_sn, device_id, pipeline_id,
				    temperature, voltage, pressure, abnormal_flag, create_time,
				    process_state, process_time, process_by, is_removed, delete_time, delete_by
				FROM pressuremonitorsys.t_monitor_data_report
				WHERE abnormal_flag > 0
				  AND is_removed = 0
				  AND create_time >= ?
				  AND create_time <= ?
				""");
		
		List<Object> params = new ArrayList<>();
		params.add(startTime);
		params.add(endTime);
		
		// 添加设备SN过滤
		if (deviceSN != null && !deviceSN.isEmpty()) {
			sql.append(" AND device_sn = ?");
			params.add(deviceSN);
		}
		
		// 添加处理状态过滤
		// ClickHouse中: 0=未处理, 1=已处理
		if (processState != null) {
			sql.append(" AND process_state = ?");
			params.add(processState ? 1 : 0);
		}
		
		sql.append(" ORDER BY create_time DESC LIMIT 10000");
		
		try {
			return clickHouseJdbcTemplate.query(sql.toString(), this::mapRowToDataReportPO, params.toArray());
		} catch (Exception e) {
			log.error("ClickHouse条件查询失败: deviceSN={}, processState={}, startTime={}, endTime={}",
					deviceSN, processState, startTime, endTime, e);
			throw new RuntimeException("ClickHouse条件查询失败", e);
		}
	}
	
	/**
	 * 更新处理状态
	 * <p>
	 * 注意: ClickHouse不支持高效的UPDATE操作
	 * 这里使用ALTER TABLE UPDATE,性能较差,仅用于少量数据更新
	 * <p>
	 * 推荐方案: 在MySQL中更新,然后通过定时任务同步到ClickHouse
	 * 或者使用ReplacingMergeTree + 插入新版本的方式
	 *
	 * @param id        数据ID
	 * @param processBy 处理人
	 */
	public void updateProcessState(Long id, String processBy) {
		// ClickHouse的UPDATE语法(性能较差,不推荐频繁使用)
		String sql = """
				ALTER TABLE pressuremonitorsys.t_monitor_data_report
				UPDATE
				    process_state = 1,
				    process_time = now(),
				    process_by = ?
				WHERE id = ?
				""";
		
		try {
			clickHouseJdbcTemplate.update(sql, processBy, id);
			log.info("ClickHouse更新处理状态成功: id={}, processBy={}", id, processBy);
		} catch (Exception e) {
			log.error("ClickHouse更新处理状态失败: id={}, processBy={}", id, processBy, e);
			// 不抛出异常,允许失败(因为MySQL是主存储)
		}
	}
	
	/**
	 * 标记为删除
	 * <p>
	 * 同样使用ALTER TABLE UPDATE,性能较差
	 *
	 * @param id       数据ID
	 * @param deleteBy 删除人
	 */
	public void markAsDeleted(Long id, String deleteBy) {
		String sql = """
				ALTER TABLE pressuremonitorsys.t_monitor_data_report
				UPDATE
				    is_removed = 1,
				    delete_time = now(),
				    delete_by = ?
				WHERE id = ?
				""";
		
		try {
			clickHouseJdbcTemplate.update(sql, deleteBy, id);
			log.info("ClickHouse标记删除成功: id={}, deleteBy={}", id, deleteBy);
		} catch (Exception e) {
			log.error("ClickHouse标记删除失败: id={}, deleteBy={}", id, deleteBy, e);
			// 不抛出异常,允许失败
		}
	}
	
	/**
	 * 将ResultSet映射为DataReportPO对象
	 * <p>
	 * 这是一个RowMapper方法,用于手动映射查询结果
	 * 当BeanPropertyRowMapper无法正确映射时使用
	 *
	 * @param rs     ResultSet对象
	 * @param rowNum 行号
	 * @return DataReportPO对象
	 */
	private DeviceDataPO mapRowToDataReportPO(ResultSet rs, int rowNum) throws SQLException {
		DeviceDataPO po = new DeviceDataPO();
		
		// 基本字段
		po.setId(rs.getLong("id"));
		po.setDeviceSN(rs.getString("device_sn"));
		po.setDeviceId(rs.getLong("device_id"));
		po.setPipelineId(rs.getLong("pipeline_id"));

		// 监控数据字段
		po.setTemperature(rs.getInt("temperature"));
		po.setVoltage(rs.getInt("voltage"));
		po.setPressure(rs.getInt("pressure"));
		po.setAbnormalFlag(rs.getString("abnormal_flag"));
		po.setCreateTime(rs.getTimestamp("create_time"));
		
		// 处理状态字段(新增)
		// 注意: ClickHouse中是UInt8(0/1),需要转换为Boolean
		try {
			int processStateInt = rs.getInt("process_state");
			po.setProcessState(processStateInt == 1);
			
			// Nullable字段需要特殊处理
			Timestamp processTime = rs.getTimestamp("process_time");
			if (processTime != null) {
				po.setProcessTime(new Date(processTime.getTime()));
			}
			po.setProcessBy(rs.getString("process_by"));
			
			int isRemovedInt = rs.getInt("is_removed");
			po.setRemoved(isRemovedInt == 1);
			
			Timestamp deleteTime = rs.getTimestamp("delete_time");
			if (deleteTime != null) {
				po.setDeleteTime(new Date(deleteTime.getTime()));
			}
			po.setDeleteBy(rs.getString("delete_by"));
		} catch (SQLException e) {
			// 如果这些字段不存在(旧版本表结构),忽略错误
			log.debug("部分字段不存在,使用默认值: {}", e.getMessage());
		}
		
		return po;
	}
	
}

