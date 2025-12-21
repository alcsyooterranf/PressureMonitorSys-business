package org.pms.domain.command.service.stateflow;

import org.pms.domain.command.model.entity.CommandExecutionEntity;
import org.pms.types.Result;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 根据目标状态处理指令响应回调
 * @create 2025/12/16
 */
public interface IStateHandler {
	
	Result onSaved(CommandExecutionEntity ctx);
	
	Result onSent(CommandExecutionEntity ctx);
	
	Result onDelivered(CommandExecutionEntity ctx);
	
	Result onCompleted(CommandExecutionEntity ctx);
	
	Result onTtlTimeout(CommandExecutionEntity ctx);
	
	Result onTimeout(CommandExecutionEntity ctx);
	
}
