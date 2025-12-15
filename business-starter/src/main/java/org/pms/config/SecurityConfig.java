package org.pms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置
 * 
 * <p>业务服务的安全策略：
 * <ul>
 *   <li>所有请求都通过网关转发，网关已完成JWT认证</li>
 *   <li>网关会将认证信息通过请求头传递给后端服务</li>
 *   <li>后端服务信任网关传递的认证信息，不再进行二次认证</li>
 *   <li>因此禁用Spring Security的默认认证机制，允许所有请求匿名访问</li>
 * </ul>
 * 
 * <p>架构说明：
 * <pre>
 * ┌─────────┐      ┌──────────────────┐      ┌──────────────────┐
 * │ 前端    │─────>│ 网关(8090)       │─────>│ 业务服务(8091)   │
 * │         │      │ - JWT认证        │      │ - 信任网关       │
 * │         │      │ - 提取用户信息   │      │ - 从Header获取   │
 * │         │      │ - 添加Header     │      │   用户信息       │
 * └─────────┘      └──────────────────┘      └──────────────────┘
 * </pre>
 *
 * @author Augment Agent
 * @version 1.0
 * @since 2025-12-15
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF（因为是内部服务，请求来自网关）
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用HTTP Basic认证
                .httpBasic(AbstractHttpConfigurer::disable)
                // 禁用登出功能
                .logout(AbstractHttpConfigurer::disable)
                // 允许所有请求匿名访问（认证已在网关完成）
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}

