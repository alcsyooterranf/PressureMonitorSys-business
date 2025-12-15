package org.pms.api.dto.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 指令响应DTO（RPC通信）
 * <p>
 * 用途：网关通过RPC将设备指令响应发送给后端服务
 * <p>
 * 与Domain层DTO的区别：
 * - Domain层：BaseDeviceCommandResponseDTO（接收AEP消息）
 * - API层：CommandResponseDTO（RPC通信，简化字段）
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/11/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class CommandResponseDTO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	/**
	 * 设备ID
	 */
	private String deviceId;
	/**
	 * 产品ID
	 */
	private String productId;
	/**
	 * 任务ID
	 */
	private Long taskId;
	/**
	 * 指令执行结果
	 */
	private JsonNode commandResult;
	/**
	 * 指令执行结果状态
	 */
	private CommandState commandState;
	/**
	 * 时间戳
	 */
	private Long timestamp;
	/**
	 * 租户ID
	 */
	private String tenantId;
	
}

