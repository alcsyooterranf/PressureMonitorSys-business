package org.pms.infrastructure.repository.clickhouse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;

/**
 * ClickHouse异常数据处理状态Repository
 * 
 * 为什么需要这个表?
 * - ClickHouse不擅长UPDATE操作(性能很差)
 * - 使用独立的状态表,通过INSERT新版本来实现"更新"效果
 * - ReplacingMergeTree引擎会自动保留最新版本的数据
 * 
 * 工作原理:
 * 1. 当用户"处理"一条异常数据时,插入一条状态记录(is_processed=1)
 * 2. 当用户"删除"一条异常数据时,插入一条状态记录(is_deleted=1)
 * 3. 查询时JOIN状态表,获取最新的处理状态
 * 
 * @author zeal
 * @since 2024-11-24
 */
@Slf4j
@Repository
public class ClickHouseStatusRepository {

    @Autowired
    @Qualifier("clickHouseJdbcTemplate")
    private JdbcTemplate clickHouseJdbcTemplate;

    /**
     * 标记异常数据为"已处理"
     * 
     * 注意:这里不是UPDATE,而是INSERT一条新记录
     * ReplacingMergeTree引擎会在后台合并时保留version最大的记录
     * 
     * @param dataId 监控数据ID
     * @param processBy 处理人
     */
    public void markAsProcessed(Long dataId, String processBy) {
        // 插入新版本的状态记录
        // version使用当前时间戳,确保是最新的
        String sql = """
            INSERT INTO pressuremonitorsys.t_abnormal_process_status 
            (data_id, is_processed, is_deleted, process_time, process_by, version) 
            VALUES (?, 1, 0, ?, ?, ?)
            """;

        try {
            long version = System.currentTimeMillis();
            clickHouseJdbcTemplate.update(sql,
                    dataId,
                    new Timestamp(System.currentTimeMillis()),
                    processBy,
                    version);

            log.info("ClickHouse标记已处理成功: dataId={}, processBy={}", dataId, processBy);
        } catch (Exception e) {
            log.error("ClickHouse标记已处理失败: dataId={}", dataId, e);
            throw new RuntimeException("ClickHouse标记已处理失败", e);
        }
    }

    /**
     * 标记异常数据为"已删除"
     * 
     * @param dataId 监控数据ID
     * @param deleteBy 删除人
     */
    public void markAsDeleted(Long dataId, String deleteBy) {
        // 插入新版本的状态记录
        String sql = """
            INSERT INTO pressuremonitorsys.t_abnormal_process_status 
            (data_id, is_processed, is_deleted, delete_time, delete_by, version) 
            VALUES (?, 0, 1, ?, ?, ?)
            """;

        try {
            long version = System.currentTimeMillis();
            clickHouseJdbcTemplate.update(sql,
                    dataId,
                    new Timestamp(System.currentTimeMillis()),
                    deleteBy,
                    version);

            log.info("ClickHouse标记已删除成功: dataId={}, deleteBy={}", dataId, deleteBy);
        } catch (Exception e) {
            log.error("ClickHouse标记已删除失败: dataId={}", dataId, e);
            throw new RuntimeException("ClickHouse标记已删除失败", e);
        }
    }

    /**
     * 查询异常数据及处理状态
     * 
     * 使用物化视图查询,性能更好
     * 如果没有创建物化视图,可以使用下面注释的SQL
     * 
     * @param deviceSN 设备序列号(可选)
     * @param processState 处理状态(可选,true=已处理,false=未处理,null=全部)
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 异常数据列表
     */
    public java.util.List<java.util.Map<String, Object>> queryAbnormalDataWithStatus(
            String deviceSN, Boolean processState, Date startTime, Date endTime) {

        // 使用物化视图查询(推荐)
        StringBuilder sql = new StringBuilder("""
            SELECT * FROM pressuremonitorsys.mv_abnormal_data_with_status
            WHERE is_deleted = 0
            """);

        // 动态构建WHERE条件
        java.util.List<Object> params = new java.util.ArrayList<>();

        if (deviceSN != null && !deviceSN.isEmpty()) {
            sql.append(" AND device_sn = ?");
            params.add(deviceSN);
        }

        if (processState != null) {
            sql.append(" AND is_processed = ?");
            params.add(processState ? 1 : 0);
        }

        if (startTime != null) {
            sql.append(" AND create_time >= ?");
            params.add(startTime);
        }

        if (endTime != null) {
            sql.append(" AND create_time <= ?");
            params.add(endTime);
        }

        sql.append(" ORDER BY create_time DESC LIMIT 1000");

        try {
            return clickHouseJdbcTemplate.queryForList(sql.toString(), params.toArray());
        } catch (Exception e) {
            log.error("ClickHouse查询异常数据失败: deviceSN={}, processState={}", deviceSN, processState, e);
            throw new RuntimeException("ClickHouse查询异常数据失败", e);
        }
    }

    /**
     * 如果没有创建物化视图,可以使用这个方法
     * 直接JOIN查询(性能稍差,但更灵活)
     */
    public java.util.List<java.util.Map<String, Object>> queryAbnormalDataWithStatusByJoin(
            String deviceSN, Boolean processState, Date startTime, Date endTime) {

        // 使用LEFT JOIN查询
        // FINAL关键字确保获取ReplacingMergeTree的最新版本
        StringBuilder sql = new StringBuilder("""
            SELECT 
                d.*,
                COALESCE(s.is_processed, 0) as is_processed,
                COALESCE(s.is_deleted, 0) as is_deleted,
                s.process_time,
                s.process_by,
                s.delete_time,
                s.delete_by
            FROM pressuremonitorsys.t_monitor_data_report d
            LEFT JOIN (
                SELECT * FROM pressuremonitorsys.t_abnormal_process_status FINAL
            ) s ON d.id = s.data_id
            WHERE d.abnormal_flag > 0
              AND COALESCE(s.is_deleted, 0) = 0
            """);

        java.util.List<Object> params = new java.util.ArrayList<>();

        if (deviceSN != null && !deviceSN.isEmpty()) {
            sql.append(" AND d.device_sn = ?");
            params.add(deviceSN);
        }

        if (processState != null) {
            sql.append(" AND COALESCE(s.is_processed, 0) = ?");
            params.add(processState ? 1 : 0);
        }

        if (startTime != null) {
            sql.append(" AND d.create_time >= ?");
            params.add(startTime);
        }

        if (endTime != null) {
            sql.append(" AND d.create_time <= ?");
            params.add(endTime);
        }

        sql.append(" ORDER BY d.create_time DESC LIMIT 1000");

        try {
            return clickHouseJdbcTemplate.queryForList(sql.toString(), params.toArray());
        } catch (Exception e) {
            log.error("ClickHouse JOIN查询异常数据失败: deviceSN={}, processState={}", deviceSN, processState, e);
            throw new RuntimeException("ClickHouse JOIN查询异常数据失败", e);
        }
    }
}

