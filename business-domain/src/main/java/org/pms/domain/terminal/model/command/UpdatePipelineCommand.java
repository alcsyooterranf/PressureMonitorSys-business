package org.pms.domain.terminal.model.command;

import lombok.Builder;
import lombok.Value;

/**
 * 更新管道命令对象
 * <p>
 * 用途：封装更新管道的参数，替代API层的PipelineUpdateReq
 *
 * @author refactor
 * @date 2025-12-18
 */
@Value
@Builder
public class UpdatePipelineCommand {
	
	/**
	 * 主键ID
	 */
	Long id;
	
	/**
	 * 管道名称
	 */
	String pipelineName;
	
	/**
	 * 位置
	 */
	String location;
	
	/**
	 * 经度
	 */
	String longitude;
	
	/**
	 * 纬度
	 */
	String latitude;
	
	/**
	 * 备注
	 */
	String remark;
	
}

