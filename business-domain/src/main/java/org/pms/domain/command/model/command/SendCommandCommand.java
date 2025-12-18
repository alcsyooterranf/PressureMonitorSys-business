package org.pms.domain.command.model.command;

import lombok.Builder;
import lombok.Value;

/**
 * 发送指令命令对象
 * <p>
 * 用途：封装发送指令的参数，替代API层的CommandTaskSendReq
 *
 * @author refactor
 * @date 2025-12-18
 */
@Value
@Builder
public class SendCommandCommand {
	
	/**
	 * 指令任务ID
	 */
	Long taskId;
	
	/**
	 * 设备ID
	 */
	Long deviceId;
	
	/**
	 * 设备SN (AEP平台的设备标识)
	 */
	Long deviceSN;
	
	/**
	 * 管道ID (AEP平台的产品标识)
	 */
	Long pipelineId;
	
}

