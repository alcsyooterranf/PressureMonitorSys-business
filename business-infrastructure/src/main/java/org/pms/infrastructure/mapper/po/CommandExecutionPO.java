package org.pms.infrastructure.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令执行过程模型
 * @create 2025/12/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandExecutionPO {
	
	private Long id;
	private Long commandTaskId;
	private String tenantId;
	private Long pipelineId;
	private Long deviceId;
	private Long deviceSN;
	private String serviceIdentifier;
	private Long aepTaskId;
	private Short status;
	private String externalErrorMsg;
	private String requestPayload;
	private String resultDetail;
	private String lastRawCallback;
	private Date lastCallbackTime;
	private Date createTime;
	private Date updateTime;
	
}
