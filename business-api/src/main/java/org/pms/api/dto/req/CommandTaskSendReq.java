package org.pms.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令下发请求
 * @create 2025/12/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandTaskSendReq {
	
	/**
	 * 指令任务ID
	 */
	private Long taskId;
	
	/**
	 * 设备ID
	 */
	private Long deviceId;
	
	/**
	 * 设备SN (AEP平台的设备标识)
	 */
	private Long deviceSN;
	
	/**
	 * 管道ID (AEP平台的产品标识)
	 */
	private Long pipelineId;
	
}

