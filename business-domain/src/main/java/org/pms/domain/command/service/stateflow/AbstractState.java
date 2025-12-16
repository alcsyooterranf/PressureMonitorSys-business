package org.pms.domain.command.service.stateflow;

import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.domain.command.repository.ICommandExecutionRepository;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 抽象状态类
 * @create 2025/12/16
 */
public abstract class AbstractState {

	protected final ICommandExecutionRepository commandExecutionRepository;

	protected AbstractState(ICommandExecutionRepository commandExecutionRepository) {
		this.commandExecutionRepository = commandExecutionRepository;
	}

	/**
	 * 状态转移到SAVED
	 */
	public abstract void saved(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

	/**
	 * 状态转移到SENT
	 */
	public abstract void sent(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

	/**
	 * 状态转移到DELIVERED
	 */
	public abstract void delivered(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

	/**
	 * 状态转移到COMPLETED
	 */
	public abstract void completed(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

	/**
	 * 状态转移到TTL_TIMEOUT
	 */
	public abstract void ttlTimeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

	/**
	 * 状态转移到TIMEOUT
	 */
	public abstract void timeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

}
