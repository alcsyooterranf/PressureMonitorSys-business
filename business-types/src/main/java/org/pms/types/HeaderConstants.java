package org.pms.types;

/**
 * HTTP Header常量定义
 *
 * <p>用于规范网关到后端的Header传递约定</p>
 *
 * @author Augment Agent
 * @since 2024-11-24
 */
public class HeaderConstants {
	
	// ==================== 用户相关 ====================
	
	/**
	 * 用户ID
	 * <p>网关在JWT鉴权成功后，将userId放入此header传递给后端</p>
	 * <p>后端可直接从header获取userId，无需再次解析JWT</p>
	 */
	public static final String USER_ID = "X-User-Id";
	
	/**
	 * 用户名
	 * <p>可选，用于日志记录和审计</p>
	 */
	public static final String USER_NAME = "X-User-Name";
	
	/**
	 * 用户角色
	 * <p>可选，多个角色用逗号分隔，例如: "admin,operator"</p>
	 */
	public static final String USER_ROLES = "X-User-Roles";
	
	// ==================== 追踪相关 ====================
	
	/**
	 * 请求追踪ID
	 * <p>用于分布式链路追踪，贯穿整个请求生命周期</p>
	 */
	public static final String REQUEST_ID = "X-Request-Id";
	
	/**
	 * 链路追踪ID
	 * <p>用于APM系统（如SkyWalking、Zipkin）的链路追踪</p>
	 */
	public static final String TRACE_ID = "X-Trace-Id";
	
	/**
	 * Span ID
	 * <p>用于APM系统的Span追踪</p>
	 */
	public static final String SPAN_ID = "X-Span-Id";
	
	// ==================== 租户相关 ====================
	
	/**
	 * 租户ID
	 * <p>用于多租户系统的租户隔离</p>
	 */
	public static final String TENANT_ID = "X-Tenant-Id";
	
	// ==================== 客户端相关 ====================
	
	/**
	 * 客户端真实IP
	 * <p>经过多层代理后的客户端真实IP</p>
	 */
	public static final String REAL_IP = "X-Real-IP";
	
	/**
	 * 转发IP链
	 * <p>记录请求经过的所有代理IP</p>
	 */
	public static final String FORWARDED_FOR = "X-Forwarded-For";
	
	/**
	 * 客户端类型
	 * <p>例如: "web", "mobile-ios", "mobile-android", "mini-program"</p>
	 */
	public static final String CLIENT_TYPE = "X-Client-Type";
	
	/**
	 * 客户端版本
	 * <p>例如: "1.0.0", "2.3.1"</p>
	 */
	public static final String CLIENT_VERSION = "X-Client-Version";
	
	// ==================== 其他 ====================
	
	/**
	 * API版本
	 * <p>用于API版本控制</p>
	 */
	public static final String API_VERSION = "X-API-Version";
	
	/**
	 * 来源系统
	 * <p>标识请求来自哪个系统，例如: "gateway", "admin-portal", "mobile-app"</p>
	 */
	public static final String SOURCE_SYSTEM = "X-Source-System";
	
	// 私有构造函数，防止实例化
	private HeaderConstants() {
		throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
	}
	
}

