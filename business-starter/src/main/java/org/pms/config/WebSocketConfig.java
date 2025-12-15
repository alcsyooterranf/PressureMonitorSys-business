package org.pms.config;

import org.pms.domain.alert.service.impl.AlertWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置类
 * <p>
 * WebSocket是什么?
 * - WebSocket是一种网络通信协议,提供全双工通信
 * - 与HTTP不同,WebSocket建立连接后可以持续通信,不需要每次都发起请求
 * - 适合实时推送场景,如聊天、通知、实时监控等
 * <p>
 * WebSocket vs HTTP轮询:
 * ┌─────────────┬──────────────┬──────────────┐
 * │             │ HTTP轮询     │ WebSocket    │
 * ├─────────────┼──────────────┼──────────────┤
 * │ 实时性      │ 差(延迟1-5秒)│ 好(毫秒级)   │
 * │ 服务器压力  │ 大(频繁请求) │ 小(长连接)   │
 * │ 网络开销    │ 大(每次HTTP) │ 小(只传数据) │
 * │ 实现复杂度  │ 简单         │ 中等         │
 * └─────────────┴──────────────┴──────────────┘
 * <p>
 * 网关代理架构(推荐):
 * ┌─────────┐      ┌──────────────────┐      ┌──────────┐
 * │ 前端    │─────>│ 网关(8090)       │─────>│ 后端     │
 * │         │      │ - JWT鉴权        │      │ (8091)   │
 * │         │      │ - 提取userId     │      │          │
 * │         │      │ - 添加X-User-Id  │      │          │
 * └─────────┘      └──────────────────┘      └──────────┘
 * <p>
 * 工作流程:
 * 1. 前端发起WebSocket连接: ws://gateway:8090/ws/alert?token=xxx
 * 2. 网关验证token,提取userId,添加到X-User-Id header
 * 3. 网关转发到后端: ws://backend:8091/ws/alert (带X-User-Id header)
 * 4. 后端从header获取userId,保存WebSocketSession
 * 5. 当有告警时,后端通过WebSocketSession推送消息到前端
 * 6. 前端收到消息,显示红灯和弹窗
 * <p>
 * 兼容模式(直连后端):
 * - 前端也可以直连后端: ws://backend:8091/ws/alert?userId=xxx
 * - 但这种方式绕过了网关鉴权,仅用于开发测试
 *
 * @author zeal
 * @since 2024-11-24
 */
@Configuration
@EnableWebSocket  // 启用WebSocket支持
public class WebSocketConfig implements WebSocketConfigurer {
	
	/**
	 * 注册WebSocket处理器
	 * <p>
	 * 这个方法会在Spring启动时被调用
	 * 用于配置WebSocket的路径、处理器、跨域等
	 *
	 * @param registry WebSocket处理器注册表
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry
				// 添加处理器
				.addHandler(alertWebSocketHandler(), "/ws/alert")
				// 允许跨域(开发环境可以用*,生产环境应该指定具体域名)
				.setAllowedOrigins("*");
		// 也可以指定具体域名:
		// .setAllowedOrigins("https://your-domain.com", "http://localhost:8080");
	}
	
	/**
	 * 创建告警WebSocket处理器Bean
	 *
	 * @Bean注解表示这是一个Spring管理的Bean Spring会自动创建这个对象, 并注入到需要的地方
	 */
	@Bean
	public AlertWebSocketHandler alertWebSocketHandler() {
		return new AlertWebSocketHandler();
	}
	
}

