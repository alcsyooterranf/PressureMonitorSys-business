package org.pms.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 多数据源配置类
 * 配置MySQL和ClickHouse两个数据源
 *
 * <p>注意事项：
 * <ul>
 *   <li>HikariCP要求使用jdbc-url而不是url</li>
 *   <li>@ConfigurationProperties直接绑定到HikariDataSource</li>
 *   <li>MySQL为主数据源，MyBatis会默认使用</li>
 *   <li>ClickHouse用于时序数据，不支持事务</li>
 * </ul>
 *
 * @author zeal
 * @since 2024-11-24
 */
@Configuration
public class DataSourceConfig {

    /**
     * MySQL数据源配置(主数据源)
     * 用于存储设备元数据、用户信息、产品信息等
     *
     * <p>配置属性绑定：
     * <ul>
     *   <li>spring.datasource.mysql.jdbc-url → jdbcUrl</li>
     *   <li>spring.datasource.mysql.driver-class-name → driverClassName</li>
     *   <li>spring.datasource.mysql.username → username</li>
     *   <li>spring.datasource.mysql.password → password</li>
     *   <li>spring.datasource.mysql.* → HikariCP的其他配置</li>
     * </ul>
     *
     * @Primary注解表示这是主数据源,当有多个同类型Bean时优先使用这个
     */
    @Bean(name = "mysqlDataSource")
    @Primary  // 主数据源,MyBatis Plus会默认使用这个
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public HikariDataSource mysqlDataSource() {
        return new HikariDataSource();
    }

    /**
     * ClickHouse数据源配置
     * 用于存储时序监控数据
     *
     * <p>配置属性绑定：
     * <ul>
     *   <li>spring.datasource.clickhouse.jdbc-url → jdbcUrl</li>
     *   <li>spring.datasource.clickhouse.driver-class-name → driverClassName</li>
     *   <li>spring.datasource.clickhouse.username → username</li>
     *   <li>spring.datasource.clickhouse.password → password</li>
     *   <li>spring.datasource.clickhouse.* → HikariCP的其他配置</li>
     * </ul>
     *
     * <p>注意:ClickHouse不支持事务,不要在事务中使用
     */
    @Bean(name = "clickHouseDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.clickhouse")
    public HikariDataSource clickHouseDataSource() {
        return new HikariDataSource();
    }

    /**
     * ClickHouse JdbcTemplate
     * 用于执行ClickHouse的SQL操作
     *
     * <p>JdbcTemplate是Spring提供的JDBC操作工具类,简化了JDBC操作
     * 相比MyBatis,JdbcTemplate更轻量,适合简单的CRUD操作
     */
    @Bean(name = "clickHouseJdbcTemplate")
    public JdbcTemplate clickHouseJdbcTemplate() {
        return new JdbcTemplate(clickHouseDataSource());
    }
}

