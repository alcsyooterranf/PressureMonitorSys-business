package org.pms.domain.alert.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.alert.model.AlertMessage;
import org.pms.types.constants.HeaderConstants;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 告警WebSocket处理器
 * 
 * 这个类负责处理WebSocket连接的生命周期:
 * 1. 连接建立(afterConnectionEstablished)
 * 2. 接收消息(handleTextMessage)
 * 3. 连接关闭(afterConnectionClosed)
 * 4. 连接异常(handleTransportError)
 * 
 * 工作流程:
 * ┌──────────┐                    ┌──────────┐
 * │  前端    │                    │  后端    │
 * └────┬─────┘                    └────┬─────┘
 *      │                               │
 *      │ 1. 建立WebSocket连接          │
 *      │ ws://localhost:8091/ws/alert?userId=123
 *      ├──────────────────────────────>│
 *      │                               │ 保存session到SESSIONS
 *      │                               │
 *      │ 2. 设备上报异常数据           │
 *      │                               │<─── 设备
 *      │                               │
 *      │                               │ 检测到异常
 *      │                               │ 构建AlertMessage
 *      │                               │
 *      │ 3. 推送告警消息               │
 *      │<──────────────────────────────┤
 *      │                               │
 *      │ 4. 显示红灯和弹窗             │
 *      │                               │
 * 
 * @author zeal
 * @since 2024-11-24
 */
@Slf4j
public class AlertWebSocketHandler extends TextWebSocketHandler {

    /**
     * 存储所有在线用户的WebSocket会话
     * 
     * 为什么使用ConcurrentHashMap?
     * - 线程安全:多个线程可以同时读写
     * - 高性能:读操作不加锁,写操作只锁部分数据
     * 
     * Key: 用户ID(从URL参数获取)
     * Value: WebSocketSession对象(用于发送消息)
     */
    private static final ConcurrentHashMap<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    /**
     * JSON序列化工具
     * 用于将AlertMessage对象转换为JSON字符串
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 连接建立时调用
     * 
     * 这个方法在前端发起WebSocket连接时被调用
     * 我们需要:
     * 1. 从URL参数中获取用户ID
     * 2. 将session保存到SESSIONS中
     * 3. 记录日志
     * 
     * @param session WebSocket会话对象
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从URL参数获取用户ID
        // 例如: ws://localhost:8091/ws/alert?userId=123
        // 解析后得到: userId=123
        String userId = getUserIdFromSession(session);

        // 保存session到Map中
        // 如果同一个用户多次连接,会覆盖旧的session
        SESSIONS.put(userId, session);

        log.info("WebSocket连接建立: userId={}, sessionId={}, 当前在线用户数: {}",
                userId, session.getId(), SESSIONS.size());

        // 可以发送欢迎消息(可选)
        // sendWelcomeMessage(session, userId);
    }

    /**
     * 接收到消息时调用
     * 
     * 这个方法在前端发送消息到服务器时被调用
     * 在告警系统中,通常不需要前端发送消息
     * 但可以用于:
     * 1. 心跳检测(前端定期发送ping,服务器回复pong)
     * 2. 用户操作(如标记告警已读)
     * 
     * @param session WebSocket会话对象
     * @param message 接收到的消息
     * @throws Exception 异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = getUserIdFromSession(session);
        String payload = message.getPayload();

        log.debug("收到WebSocket消息: userId={}, message={}", userId, payload);

        // 处理心跳消息
        if ("ping".equals(payload)) {
            session.sendMessage(new TextMessage("pong"));
            return;
        }

        // 处理其他消息(根据业务需求)
        // 例如:前端发送{"action":"markRead","alertId":123}
        // 可以在这里处理标记已读的逻辑
    }

    /**
     * 连接关闭时调用
     * 
     * 这个方法在WebSocket连接关闭时被调用
     * 原因可能是:
     * 1. 前端主动关闭(用户关闭页面)
     * 2. 网络断开
     * 3. 服务器主动关闭
     * 
     * @param session WebSocket会话对象
     * @param status 关闭状态
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);

        // 从Map中移除session
        SESSIONS.remove(userId);

        log.info("WebSocket连接关闭: userId={}, sessionId={}, status={}, 当前在线用户数: {}",
                userId, session.getId(), status, SESSIONS.size());
    }

    /**
     * 连接异常时调用
     * 
     * 这个方法在WebSocket连接出现异常时被调用
     * 例如:网络错误、消息格式错误等
     * 
     * @param session WebSocket会话对象
     * @param exception 异常对象
     * @throws Exception 异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String userId = getUserIdFromSession(session);

        log.error("WebSocket连接异常: userId={}, sessionId={}",
                userId, session.getId(), exception);

        // 关闭异常的连接
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }

        // 从Map中移除session
        SESSIONS.remove(userId);
    }

    /**
     * 推送告警消息到指定用户
     * 
     * 这个方法会被告警服务调用
     * 当检测到设备异常时,推送消息到对应的用户
     * 
     * @param userId 用户ID
     * @param alert 告警消息对象
     */
    public void sendAlertToUser(String userId, AlertMessage alert) {
        // 从Map中获取用户的session
        WebSocketSession session = SESSIONS.get(userId);

        // 检查session是否存在且连接是否打开
        if (session == null) {
            log.warn("用户未连接WebSocket,无法推送告警: userId={}", userId);
            return;
        }

        if (!session.isOpen()) {
            log.warn("WebSocket连接已关闭,无法推送告警: userId={}", userId);
            SESSIONS.remove(userId);  // 清理无效session
            return;
        }

        try {
            // 将AlertMessage对象转换为JSON字符串
            String json = OBJECT_MAPPER.writeValueAsString(alert);

            // 发送消息
            // TextMessage是WebSocket的文本消息类型
            session.sendMessage(new TextMessage(json));

            log.info("告警推送成功: userId={}, deviceSN={}, alertType={}",
                    userId, alert.getDeviceSN(), alert.getAlertType());

        } catch (IOException e) {
            log.error("告警推送失败: userId={}, deviceSN={}",
                    userId, alert.getDeviceSN(), e);

            // 发送失败,可能是连接已断开,清理session
            SESSIONS.remove(userId);
        }
    }

    /**
     * 广播告警消息到所有在线用户
     * 
     * 使用场景:
     * 1. 严重告警需要通知所有管理员
     * 2. 系统级告警
     * 
     * @param alert 告警消息对象
     */
    public void broadcastAlert(AlertMessage alert) {
        log.info("广播告警消息: deviceSN={}, alertType={}, 在线用户数: {}",
                alert.getDeviceSN(), alert.getAlertType(), SESSIONS.size());

        // 遍历所有在线用户
        SESSIONS.forEach((userId, session) -> {
            // 检查连接是否打开
            if (session.isOpen()) {
                try {
                    String json = OBJECT_MAPPER.writeValueAsString(alert);
                    session.sendMessage(new TextMessage(json));
                    log.debug("广播告警成功: userId={}", userId);
                } catch (IOException e) {
                    log.error("广播告警失败: userId={}", userId, e);
                }
            } else {
                // 清理无效session
                SESSIONS.remove(userId);
            }
        });
    }

    /**
     * 从WebSocketSession中获取用户ID
     *
     * 支持两种方式获取userId:
     * 1. 从网关传递的header中获取(推荐) - 网关鉴权后会将userId放入X-User-Id header
     * 2. 从URL参数中获取(兼容旧方式) - ws://localhost:8091/ws/alert?userId=123
     *
     * 网关代理架构:
     * ┌─────────┐      ┌──────────────────┐      ┌──────────┐
     * │ 前端    │─────>│ 网关(8090)       │─────>│ 后端     │
     * │         │      │ - JWT鉴权        │      │ (8091)   │
     * │         │      │ - 提取userId     │      │          │
     * │         │      │ - 添加X-User-Id  │      │          │
     * └─────────┘      └──────────────────┘      └──────────┘
     *
     * 前端连接方式:
     * ws://gateway:8090/ws/alert?token=xxx
     *
     * 网关处理后转发:
     * ws://backend:8091/ws/alert (添加header: X-User-Id=123)
     *
     * @param session WebSocket会话对象
     * @return 用户ID
     */
    private String getUserIdFromSession(WebSocketSession session) {
        try {
            // 方式1: 优先从header中获取userId(网关传递)
            // 网关在鉴权成功后会将userId放入X-User-Id header
            org.springframework.http.HttpHeaders headers = session.getHandshakeHeaders();
            String userIdFromHeader = headers.getFirst(HeaderConstants.USER_ID);

            if (userIdFromHeader != null && !userIdFromHeader.isEmpty()) {
                log.debug("从header获取userId: {}", userIdFromHeader);
                return userIdFromHeader;
            }

            // 方式2: 从URL参数中获取userId(兼容旧方式,直连后端时使用)
            // 例如: ws://localhost:8091/ws/alert?userId=123
            URI uri = session.getUri();
            if (uri == null) {
                log.warn("无法获取URI,使用匿名用户");
                return "anonymous";
            }

            String query = uri.getQuery();
            if (query == null || query.isEmpty()) {
                log.warn("URL参数为空且header中无userId,使用匿名用户");
                return "anonymous";
            }

            // 解析查询参数
            String userIdFromQuery = Arrays.stream(query.split("&"))
                    .filter(param -> param.startsWith("userId="))
                    .map(param -> param.substring(7))
                    .findFirst()
                    .orElse("anonymous");

            log.debug("从URL参数获取userId: {}", userIdFromQuery);
            return userIdFromQuery;

        } catch (Exception e) {
            log.error("解析用户ID失败", e);
            return "anonymous";
        }
    }

    /**
     * 获取当前在线用户数
     * 用于监控
     */
    public int getOnlineUserCount() {
        return SESSIONS.size();
    }

    /**
     * 获取所有在线用户ID
     * 用于监控和管理
     */
    public java.util.Set<String> getOnlineUserIds() {
        return SESSIONS.keySet();
    }

    /**
     * 关闭指定用户的连接
     * 用于管理功能(如踢出用户)
     */
    public void closeUserConnection(String userId) {
        WebSocketSession session = SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.close(CloseStatus.NORMAL);
                SESSIONS.remove(userId);
                log.info("关闭用户WebSocket连接: userId={}", userId);
            } catch (IOException e) {
                log.error("关闭WebSocket连接失败: userId={}", userId, e);
            }
        }
    }
}

