package org.pms.domain.command.service.stateflow.impl;

import jakarta.annotation.Resource;
import org.pms.domain.command.model.entity.CommandExecutionEntity;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.domain.command.repository.ICommandExecutionRepository;
import org.pms.domain.command.service.stateflow.AbstractState;
import org.pms.domain.command.service.stateflow.CommandTransitionCtx;
import org.pms.domain.command.service.stateflow.IStateHandler;
import org.pms.domain.command.service.stateflow.StateConfig;
import org.pms.types.BizCode;
import org.pms.types.Result;
import org.springframework.stereotype.Service;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 状态流转处理实现类
 * @create 2025/12/16
 */
@Service
public class StateHandlerImpl extends StateConfig implements IStateHandler {
	
	@Resource
	protected ICommandExecutionRepository commandExecutionRepository;
	
	private Result dispatch(CommandExecutionStatusVO target, CommandExecutionEntity ctx) {
		// currentStatus 由 DB 决定, 为了幂等和防乱序
		CommandExecutionStatusVO current = commandExecutionRepository.selectByAepTaskIdAndDeviceId(ctx.getAepTaskId(), ctx.getDeviceId()).getStatus();
		AbstractState state = stateGroup.get(current);
		
		if (state == null) {
			return Result.buildResult(BizCode.COMMAND_STATE_UNKNOWN);
		}
		return state.apply(ctx, target);
	}
	
	@Override
	public Result onSaved(CommandExecutionEntity ctx) {
		return dispatch(CommandExecutionStatusVO.SAVED, ctx);
	}
	
	@Override
	public Result onSent(CommandExecutionEntity ctx) {
		return dispatch(CommandExecutionStatusVO.SENT, ctx);
	}
	
	@Override
	public Result onDelivered(CommandExecutionEntity ctx) {
		return dispatch(CommandExecutionStatusVO.DELIVERED, ctx);
	}
	
	@Override
	public Result onCompleted(CommandExecutionEntity ctx) {
		return dispatch(CommandExecutionStatusVO.COMPLETED, ctx);
	}
	
	@Override
	public Result onTtlTimeout(CommandExecutionEntity ctx) {
		return dispatch(CommandExecutionStatusVO.TTL_TIMEOUT, ctx);
	}
	
	@Override
	public Result onTimeout(CommandExecutionEntity ctx) {
		return dispatch(CommandExecutionStatusVO.TIMEOUT, ctx);
	}
	
}
