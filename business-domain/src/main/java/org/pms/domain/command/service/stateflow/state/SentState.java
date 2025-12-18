package org.pms.domain.command.service.stateflow.state;

import lombok.extern.slf4j.Slf4j;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.domain.command.repository.ICommandExecutionRepository;
import org.pms.domain.command.service.stateflow.AbstractState;
import org.pms.types.BizCode;
import org.pms.types.exception.BizException;
import org.springframework.stereotype.Component;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description SENT状态
 * 可以转移到: DELIVERED, TIMEOUT
 * @create 2025/12/16
 */
@Slf4j
@Component
public class SentState extends AbstractState {

	public SentState(ICommandExecutionRepository commandExecutionRepository) {
		super(commandExecutionRepository);
	}

	@Override
	public void saved(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// SENT -> SAVED (不允许)
		log.error("非法的状态流转: {} -> SAVED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new BizException(BizCode.COMMAND_STATE_TRANSITION_ILLEGAL, "非法的状态流转: " + currentState + " -> SAVED");
	}

	@Override
	public void sent(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// SENT -> SENT (不允许)
		log.error("非法的状态流转: {} -> SENT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new BizException(BizCode.COMMAND_STATE_TRANSITION_ILLEGAL, "非法的状态流转: " + currentState + " -> SENT");
	}

	@Override
	public void delivered(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// SENT -> DELIVERED (允许)
		boolean success = commandExecutionRepository.updateStatus(aepTaskId, deviceId, CommandExecutionStatusVO.DELIVERED);
		if (success) {
			log.info("指令状态流转成功: {} -> DELIVERED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		} else {
			log.error("指令状态流转失败: {} -> DELIVERED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
			throw new BizException(BizCode.COMMAND_STATE_TRANSITION_FAILED);
		}
	}

	@Override
	public void completed(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// SENT -> COMPLETED (不允许)
		log.error("非法的状态流转: {} -> COMPLETED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new BizException(BizCode.COMMAND_STATE_TRANSITION_ILLEGAL, "非法的状态流转: " + currentState + " -> COMPLETED");
	}

	@Override
	public void ttlTimeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// SENT -> TTL_TIMEOUT (不允许)
		log.error("非法的状态流转: {} -> TTL_TIMEOUT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new BizException(BizCode.COMMAND_STATE_TRANSITION_ILLEGAL, "非法的状态流转: " + currentState + " -> TTL_TIMEOUT");
	}

	@Override
	public void timeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// SENT -> TIMEOUT (允许)
		boolean success = commandExecutionRepository.updateStatus(aepTaskId, deviceId, CommandExecutionStatusVO.TIMEOUT);
		if (success) {
			log.info("指令状态流转成功: {} -> TIMEOUT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		} else {
			log.error("指令状态流转失败: {} -> TIMEOUT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
			throw new BizException(BizCode.COMMAND_STATE_TRANSITION_FAILED);
		}
	}

}

