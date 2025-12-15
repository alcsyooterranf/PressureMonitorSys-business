package org.pms.infrastructure.repository.persistence;

import com.pms.types.AppException;
import com.pms.types.ResponseCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.alert.service.AlertService;
import org.pms.domain.devicedata.model.entity.DeviceDataEntity;
import org.pms.domain.devicedata.repository.IDeviceDataRepository;
import org.pms.infrastructure.adapter.DeviceDataConverter;
import org.pms.infrastructure.mapper.IDeviceDataMapper;
import org.pms.infrastructure.mapper.IDeviceMapper;
import org.pms.infrastructure.mapper.po.DeviceDataPO;
import org.pms.infrastructure.mapper.po.DevicePO;
import org.pms.infrastructure.repository.clickhouse.ClickHouseDataBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

/**
 * 数据上报仓储实现类
 * 职责:
 * 1. 保存监控数据到MySQL(用于业务查询)
 * 2. 异步保存监控数据到ClickHouse(用于大数据分析)
 * 3. 触发告警服务(微信推送 + WebSocket推送)
 *
 * @author alcsyooterranf
 * @since 2024-11-24
 */
@Slf4j
@Repository
public class DeviceDataRepository implements IDeviceDataRepository {
	
	@Resource
	private DeviceDataConverter deviceDataConverter;
	@Resource
	private IDeviceDataMapper deviceDataMapper;
	@Resource
	private IDeviceMapper deviceMapper;
	
	/**
	 * ClickHouse数据缓冲区
	 * 用于异步批量写入ClickHouse*
	 * 为什么使用@Autowired而不是构造器注入?
	 * - ClickHouse是可选的,如果没有配置ClickHouse,这个Bean可能不存在
	 * - 使用@Autowired(required=false)可以避免启动失败
	 */
	@Autowired(required=false)
	private ClickHouseDataBuffer clickHouseDataBuffer;
	
	/**
	 * 告警服务
	 * 用于发送微信推送和WebSocket推送
	 */
	@Resource
	private AlertService alertService;
	
	@Override
	public int updateStatusById(Long id, String operatorName) {
		int updateAbnormalReportCount = deviceDataMapper.updateStateById(operatorName, id);
		if (1 != updateAbnormalReportCount) {
			throw new AppException(ResponseCode.ABNORMAL_REPORT_ID_ERROR.getCode(),
					ResponseCode.ABNORMAL_REPORT_ID_ERROR.getMessage());
		}
		return updateAbnormalReportCount;
	}
	
	@Override
	public int deleteDeviceDataById(Long id, String operatorName) {
		int deleteCnt = deviceDataMapper.deleteDataReportById(operatorName, id);
		if (1 != deleteCnt) {
			throw new AppException(ResponseCode.ABNORMAL_REPORT_ID_ERROR.getCode(),
					ResponseCode.ABNORMAL_REPORT_ID_ERROR.getMessage());
		}
		return deleteCnt;
	}
	
	/**
	 * 插入监控数据
	 * 这个方法是数据上报的核心入口
	 * 完整流程:
	 * 1. 转换DTO为PO对象
	 * 2. 查询设备信息(获取阈值)
	 * 3. 计算异常标志(abnormalFlag)
	 * 4. 保存到MySQL(同步,保证数据一致性)
	 * 5. 异步保存到ClickHouse(不阻塞主流程)
	 * 6. 如果有异常,触发告警服务
	 *
	 * @param deviceDataEntity 设备上报的数据
	 */
	@Override
	public void addDeviceData(DeviceDataEntity deviceDataEntity) {
		// 1. 转换DTO为PO对象
		DeviceDataPO deviceDataPO = deviceDataConverter.entity2po(deviceDataEntity);
		
		// 2. 查询设备信息(包含阈值配置)
		DevicePO devicePO = deviceMapper.queryParameterLimitsBySN(deviceDataPO.getDeviceSN());
		if (ObjectUtils.isEmpty(devicePO)) {
			log.error("设备不存在: deviceSN={}", deviceDataPO.getDeviceSN());
			throw new AppException(ResponseCode.DEVICE_SN_ERROR.getCode());
		}
		
		// 3. 计算异常标志
		// computeAbnormalFlag方法会比较实际值和阈值,设置abnormalFlag位标志
		deviceDataPO.computeAbnormalFlag(devicePO);
		deviceDataPO.setDeviceId(devicePO.getId());
		
		// 4. 保存到MySQL(同步)
		// 这是主要的数据存储,必须保证成功
		deviceDataMapper.insertDeviceData(deviceDataPO);
		log.debug("MySQL保存成功: deviceSN={}, abnormalFlag={}",
				deviceDataPO.getDeviceSN(), deviceDataPO.getAbnormalFlag());
		
		// 5. 异步保存到ClickHouse(可选)
		// 如果ClickHouse配置了,就异步写入
		// 即使ClickHouse写入失败,也不影响MySQL数据
		if (clickHouseDataBuffer != null) {
			try {
				clickHouseDataBuffer.add(deviceDataPO);
				log.debug("ClickHouse缓冲区添加成功: deviceSN={}", deviceDataPO.getDeviceSN());
			} catch (Exception e) {
				// ClickHouse写入失败不影响主流程
				log.error("ClickHouse缓冲区添加失败: deviceSN={}", deviceDataPO.getDeviceSN(), e);
			}
		}
		
		// 6. 触发告警服务(如果有异常)
		// abnormalFlag > 0 表示有异常
		if (deviceDataPO.getAbnormalFlag() > 0 && alertService != null) {
			// TODO: 后续解决
//			try {
//				log.info("触发告警服务: deviceSN={}, abnormalFlag={}",
//						deviceDataPO.getDeviceSN(), deviceDataPO.getAbnormalFlag());
//				alertService.processDataAlert(deviceDataPO, devicePO);
//			} catch (Exception e) {
//				// 告警失败不影响数据保存
//				log.error("告警服务执行失败: deviceSN={}", deviceDataPO.getDeviceSN(), e);
//			}
		}
	}
	
}
