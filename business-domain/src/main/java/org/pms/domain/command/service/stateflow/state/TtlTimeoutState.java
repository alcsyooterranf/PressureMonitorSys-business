package org.pms.domain.command.service.stateflow.state;

import lombok.extern.slf4j.Slf4j;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.domain.command.service.stateflow.AbstractState;
import org.springframework.stereotype.Component;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description TTL_TIMEOUT状态（终态）
 * 不可转移到任何状态
 * @create 2025/12/16
 */
@Slf4j
@Component
public class TtlTimeoutState extends AbstractState {
	
	@Override
	public CommandExecutionStatusVO current() {
		return CommandExecutionStatusVO.TTL_TIMEOUT;
	}
	
}

