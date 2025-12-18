package org.pms.domain.terminal.model.command;

import lombok.Builder;
import lombok.Value;

/**
 * 创建管道命令对象
 * <p>
 * 用途：封装创建管道的参数，替代API层的PipelineInsertReq
 *
 * @author refactor
 * @date 2025-12-18
 */
@Value
@Builder
public class CreatePipelineCommand {
	
	/**
	 * 管道SN
	 */
	Long pipelineSN;
	
	/**
	 * 管道名称
	 */
	String pipelineName;
	
	/**
	 * 客户账号
	 */
	String customerAccount;
	
	/**
	 * 位置
	 */
	String location;
	
	/**
	 * 备注
	 */
	String remark;
	
}

