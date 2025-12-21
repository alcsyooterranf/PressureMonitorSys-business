package org.pms.domain.command.service.stateflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令状态迁移上下文
 * @create 2025/12/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandTransitionCtx {
	
	public Long aepTaskId;
	public Long deviceId;
	private Long callbackTimestamp;
	private String lastRawCallback;
	private String resultDetail;
	
}
