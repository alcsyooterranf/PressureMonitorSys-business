package org.pms.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 创建指令任务请求
 * @create 2025/12/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandTaskCreateReq {
	
	/**
	 * 租户ID
	 */
	private String tenantId;
	
	/**
	 * 管道ID
	 */
	private Long pipelineId;
	
	/**
	 * 服务标识符
	 */
	private String serviceIdentifier;
	
	/**
	 * 指令名称
	 */
	private String name;
	
	/**
	 * 指令参数 (JSON字符串)
	 */
	private String args;
	
	/**
	 * 创建人
	 */
	private String createBy;
	
}

