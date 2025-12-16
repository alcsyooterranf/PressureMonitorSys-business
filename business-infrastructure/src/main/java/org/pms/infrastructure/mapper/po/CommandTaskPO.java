package org.pms.infrastructure.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令任务模型
 * @create 2025/12/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandTaskPO {
	
	private Long id;
	private String tenantId;
	private Long pipelineId;
	private String serviceIdentifier;
	private String name;
	private String args;
	private String createBy;
	private Date createTime;
	private Date updateTime;
	
}
