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
 * @description DELIVERED状态
 * 可以转移到: COMPLETED
 * @create 2025/12/16
 */
@Slf4j
@Component
public class DeliveredState extends AbstractState {

	public DeliveredState(ICommandExecutionRepository commandExecutionRepository) {
		super(commandExecutionRepository);
	}

	@Override
	public void saved(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// DELIVERED -> SAVED (不允许)
		log.error("非法的状态流转: {} -> SAVED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new BizException(BizCode.COMMAND_STATE_TRANSITION_ILLEGAL, "非法的状态流转: " + currentState + " -> SAVED");
	}

	@Override
	public void sent(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// DELIVERED -> SENT (不允许)
		log.error("非法的状态流转: {} -> SENT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new BizException(BizCode.COMMAND_STATE_TRANSITION_ILLEGAL, "非法的状态流转: " + currentState + " -> SENT");
	}

	@Override
	public void delivered(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// DELIVERED -> DELIVERED (不允许)
		log.error("非法的状态流转: {} -> DELIVERED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new BizException(BizCode.COMMAND_STATE_TRANSITION_ILLEGAL, "非法的状态流转: " + currentState + " -> DELIVERED");
	}

	@Override
	public void completed(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// DELIVERED -> COMPLETED (允许)
		boolean success = commandExecutionRepository.updateStatus(aepTaskId, deviceId, CommandExecutionStatusVO.COMPLETED);
		if (success) {
			log.info("指令状态流转成功: {} -> COMPLETED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		} else {
			log.error("指令状态流转失败: {} -> COMPLETED, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
			throw new BizException(BizCode.COMMAND_STATE_TRANSITION_FAILED);
		}
	}

	@Override
	public void ttlTimeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// DELIVERED -> TTL_TIMEOUT (不允许)
		log.error("非法的状态流转: {} -> TTL_TIMEOUT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new BizException(BizCode.COMMAND_STATE_TRANSITION_ILLEGAL, "非法的状态流转: " + currentState + " -> TTL_TIMEOUT");
	}

	@Override
	public void timeout(Long aepTaskId, Long deviceId, CommandExecutionStatusVO currentState) {
		// DELIVERED -> TIMEOUT (不允许)
		log.error("非法的状态流转: {} -> TIMEOUT, aepTaskId={}, deviceId={}", currentState, aepTaskId, deviceId);
		throw new BizException(BizCode.COMMAND_STATE_TRANSITION_ILLEGAL, "非法的状态流转: " + currentState + " -> TIMEOUT");
	}

}

