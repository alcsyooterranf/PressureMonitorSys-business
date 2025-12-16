package org.pms.domain.command.service.stateflow;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.domain.command.service.stateflow.state.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 状态流转配置
 * @create 2025/12/16
 */
@Slf4j
@Component
public class StateConfig {

	private final InitializedState initializedState;
	private final SavedState savedState;
	private final SentState sentState;
	private final DeliveredState deliveredState;
	private final CompletedState completedState;
	private final TtlTimeoutState ttlTimeoutState;
	private final TimeoutState timeoutState;

	protected Map<CommandExecutionStatusVO, AbstractState> stateGroup = new ConcurrentHashMap<>();

	public StateConfig(InitializedState initializedState, SavedState savedState, SentState sentState,
	                   DeliveredState deliveredState, CompletedState completedState,
	                   TtlTimeoutState ttlTimeoutState, TimeoutState timeoutState) {
		this.initializedState = initializedState;
		this.savedState = savedState;
		this.sentState = sentState;
		this.deliveredState = deliveredState;
		this.completedState = completedState;
		this.ttlTimeoutState = ttlTimeoutState;
		this.timeoutState = timeoutState;
	}

	@PostConstruct
	public void init() {
		stateGroup.put(CommandExecutionStatusVO.INITIALIZED, initializedState);
		stateGroup.put(CommandExecutionStatusVO.SAVED, savedState);
		stateGroup.put(CommandExecutionStatusVO.SENT, sentState);
		stateGroup.put(CommandExecutionStatusVO.DELIVERED, deliveredState);
		stateGroup.put(CommandExecutionStatusVO.COMPLETED, completedState);
		stateGroup.put(CommandExecutionStatusVO.TTL_TIMEOUT, ttlTimeoutState);
		stateGroup.put(CommandExecutionStatusVO.TIMEOUT, timeoutState);
		log.info("状态机配置初始化完成");
	}

	public AbstractState getState(CommandExecutionStatusVO status) {
		return stateGroup.get(status);
	}

}
