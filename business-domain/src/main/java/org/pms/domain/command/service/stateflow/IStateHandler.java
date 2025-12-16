package org.pms.domain.command.service.stateflow;

import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 状态流转处理接口
 * 所有方法的入参均为aepTaskId、deviceId和当前状态
 * 每一个方法名对应一个抽象的状态(State)
 * 通过继承抽象类AbstractState可以限制不同状态流转的具体逻辑
 * @create 2025/12/16
 */
public interface IStateHandler {

	/**
	 * 状态转移到SAVED
	 */
	void saved(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

	/**
	 * 状态转移到SENT
	 */
	void sent(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

	/**
	 * 状态转移到DELIVERED
	 */
	void delivered(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

	/**
	 * 状态转移到COMPLETED
	 */
	void completed(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

	/**
	 * 状态转移到TTL_TIMEOUT
	 */
	void ttlTimeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

	/**
	 * 状态转移到TIMEOUT
	 */
	void timeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState);

}
