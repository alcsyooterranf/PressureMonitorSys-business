package org.pms.domain.command.model.command;

import lombok.Builder;
import lombok.Value;

/**
 * 更新指令元数据命令对象
 * <p>
 * 用途：封装更新指令元数据的参数，替代API层的CommandMetaUpdateReq
 *
 * @author refactor
 * @date 2025-12-18
 */
@Value
@Builder
public class UpdateCommandMetaCommand {
	
	/**
	 * 主键ID
	 */
	Long id;
	
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

