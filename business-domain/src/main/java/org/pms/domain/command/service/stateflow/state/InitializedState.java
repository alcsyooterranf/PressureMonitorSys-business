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
 * @description INITIALIZED状态
 * 可以转移到: SAVED, SENT
 * @create 2025/12/16
 */
@Slf4j
@Component
public class InitializedState extends AbstractState {

	public InitializedState(ICommandExecutionRepository commandExecutionRepository) {
		super(commandExecutionRepository);
	}

	@Override
	public void saved(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// INITIALIZED -> SAVED (允许)
		boolean success = commandExecutionRepository.updateStatus(aepTaskId, deviceId, CommandExecutionStatusVO.SAVED);
		if (success) {
			log.info("指令状态流转成功: {} -> SAVED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		} else {
			log.error("指令状态流转失败: {} -> SAVED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
			throw new AppException("ERR_BIZ_COMMAND_STATE_4001", "指令状态流转失败");
		}
	}

	@Override
	public void sent(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// INITIALIZED -> SENT (允许)
		boolean success = commandExecutionRepository.updateStatus(aepTaskId, deviceId, CommandExecutionStatusVO.SENT);
		if (success) {
			log.info("指令状态流转成功: {} -> SENT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		} else {
			log.error("指令状态流转失败: {} -> SENT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
			throw new AppException("ERR_BIZ_COMMAND_STATE_4001", "指令状态流转失败");
		}
	}

	@Override
	public void delivered(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// INITIALIZED -> DELIVERED (不允许)
		log.error("非法的状态流转: {} -> DELIVERED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new AppException("ERR_BIZ_COMMAND_STATE_4002", "非法的状态流转: " + currentState + " -> DELIVERED");
	}

	@Override
	public void completed(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// INITIALIZED -> COMPLETED (不允许)
		log.error("非法的状态流转: {} -> COMPLETED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new AppException("ERR_BIZ_COMMAND_STATE_4002", "非法的状态流转: " + currentState + " -> COMPLETED");
	}

	@Override
	public void ttlTimeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// INITIALIZED -> TTL_TIMEOUT (不允许)
		log.error("非法的状态流转: {} -> TTL_TIMEOUT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new AppException("ERR_BIZ_COMMAND_STATE_4002", "非法的状态流转: " + currentState + " -> TTL_TIMEOUT");
	}

	@Override
	public void timeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// INITIALIZED -> TIMEOUT (不允许)
		log.error("非法的状态流转: {} -> TIMEOUT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new AppException("ERR_BIZ_COMMAND_STATE_4002", "非法的状态流转: " + currentState + " -> TIMEOUT");
	}

}

