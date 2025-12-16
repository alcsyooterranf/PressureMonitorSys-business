package org.pms.domain.command.service.stateflow.state;

import com.pms.types.AppException;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.domain.command.repository.ICommandExecutionRepository;
import org.pms.domain.command.service.stateflow.AbstractState;
import org.springframework.stereotype.Component;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description TIMEOUT状态（终态）
 * 不可转移到任何状态
 * @create 2025/12/16
 */
@Slf4j
@Component
public class TimeoutState extends AbstractState {

	public TimeoutState(ICommandExecutionRepository commandExecutionRepository) {
		super(commandExecutionRepository);
	}

	@Override
	public void saved(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// TIMEOUT -> SAVED (不允许，终态)
		log.error("终态不可流转: {} -> SAVED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new AppException("ERR_BIZ_COMMAND_STATE_4003", "终态不可流转: " + currentState + " -> SAVED");
	}

	@Override
	public void sent(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// TIMEOUT -> SENT (不允许，终态)
		log.error("终态不可流转: {} -> SENT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new AppException("ERR_BIZ_COMMAND_STATE_4003", "终态不可流转: " + currentState + " -> SENT");
	}

	@Override
	public void delivered(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// TIMEOUT -> DELIVERED (不允许，终态)
		log.error("终态不可流转: {} -> DELIVERED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new AppException("ERR_BIZ_COMMAND_STATE_4003", "终态不可流转: " + currentState + " -> DELIVERED");
	}

	@Override
	public void completed(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// TIMEOUT -> COMPLETED (不允许，终态)
		log.error("终态不可流转: {} -> COMPLETED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new AppException("ERR_BIZ_COMMAND_STATE_4003", "终态不可流转: " + currentState + " -> COMPLETED");
	}

	@Override
	public void ttlTimeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// TIMEOUT -> TTL_TIMEOUT (不允许，终态)
		log.error("终态不可流转: {} -> TTL_TIMEOUT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new AppException("ERR_BIZ_COMMAND_STATE_4003", "终态不可流转: " + currentState + " -> TTL_TIMEOUT");
	}

	@Override
	public void timeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// TIMEOUT -> TIMEOUT (不允许，终态)
		log.error("终态不可流转: {} -> TIMEOUT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new AppException("ERR_BIZ_COMMAND_STATE_4003", "终态不可流转: " + currentState + " -> TIMEOUT");
	}

}

