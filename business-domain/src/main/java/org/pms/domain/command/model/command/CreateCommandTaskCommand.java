package org.pms.domain.command.model.command;

import lombok.Builder;
import lombok.Value;

/**
 * 创建指令任务命令对象
 * <p>
 * 用途：封装创建指令任务的参数，替代API层的CommandTaskCreateReq
 *
 * @author refactor
 * @date 2025-12-18
 */
@Value
@Builder
public class CreateCommandTaskCommand {

	/**
	 * 租户ID
	 */
	String tenantId;

	/**
	 * 管道ID
	 */
	Long pipelineId;

	/**
	 * 服务标识符
	 */
	String serviceIdentifier;

	/**
	 * 指令名称
	 */
	String name;

	/**
	 * 指令参数 (JSON字符串)
	 */
	String args;

	/**
	 * 创建人
	 */
	String createBy;

}

