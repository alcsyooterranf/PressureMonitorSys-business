package org.pms.domain.command.service.stateflow.impl;

import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.domain.command.service.stateflow.IStateHandler;
import org.pms.domain.command.service.stateflow.StateConfig;
import org.pms.domain.command.service.stateflow.state.*;
import org.springframework.stereotype.Service;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 状态流转处理实现类
 * @create 2025/12/16
 */
@Service
public class StateHandlerImpl extends StateConfig implements IStateHandler {

	public StateHandlerImpl(InitializedState initializedState, SavedState savedState, SentState sentState,
	                        DeliveredState deliveredState, CompletedState completedState,
	                        TtlTimeoutState ttlTimeoutState, TimeoutState timeoutState) {
		super(initializedState, savedState, sentState, deliveredState, completedState, ttlTimeoutState, timeoutState);
	}

	@Override
	public void saved(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		stateGroup.get(currentState).saved(aepTaskId, deviceId, currentState);
	}

	@Override
	public void sent(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		stateGroup.get(currentState).sent(aepTaskId, deviceId, currentState);
	}

	@Override
	public void delivered(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		stateGroup.get(currentState).delivered(aepTaskId, deviceId, currentState);
	}

	@Override
	public void completed(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		stateGroup.get(currentState).completed(aepTaskId, deviceId, currentState);
	}

	@Override
	public void ttlTimeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		stateGroup.get(currentState).ttlTimeout(aepTaskId, deviceId, currentState);
	}

	@Override
	public void timeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		stateGroup.get(currentState).timeout(aepTaskId, deviceId, currentState);
	}

}
