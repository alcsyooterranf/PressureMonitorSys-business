package org.pms.domain.command.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;

import java.util.Date;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令执行实体
 * @create 2025/12/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandExecutionEntity {
	
	private Long id;
	private Long commandTaskId;
	private String tenantId;
	private Long pipelineId;
	private Long deviceId;
	private String serviceIdentifier;
	private Long aepTaskId;
	private CommandExecutionStatusVO status;
	private String externalErrorMsg;
	private String requestPayload;
	private String resultDetail;
	private String resultCallback;
	private Date sentTime;
	private Date lastCallbackTime;
	private Date createTime;
	private Date updateTime;
	
}

