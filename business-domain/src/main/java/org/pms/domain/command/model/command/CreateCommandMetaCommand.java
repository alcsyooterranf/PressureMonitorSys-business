package org.pms.domain.command.model.command;

import lombok.Builder;
import lombok.Value;

/**
 * 创建指令元数据命令对象
 * <p>
 * 用途：封装创建指令元数据的参数，替代API层的CommandMetaInsertReq
 * <p>
 * 特点：
 * - 不可变对象（@Value）
 * - 只包含数据，没有业务逻辑
 * - 表达用户的操作意图
 *
 * @author refactor
 * @date 2025-12-18
 */
@Value
@Builder
public class CreateCommandMetaCommand {
	
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
	 * payload schema (JSON字符串)
	 */
	String payloadSchema;
	
	/**
	 * 备注
	 */
	String remark;
	
}

