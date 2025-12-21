package org.pms.domain.command.service.stateflow;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
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
	
	protected Map<Enum<CommandExecutionStatusVO>, AbstractState> stateGroup = new ConcurrentHashMap<>();
	@Resource
	private SavedState savedState;
	@Resource
	private SentState sentState;
	@Resource
	private DeliveredState deliveredState;
	@Resource
	private CompletedState completedState;
	@Resource
	private TtlTimeoutState ttlTimeoutState;
	@Resource
	private TimeoutState timeoutState;
	
	@PostConstruct
	public void init() {
		stateGroup.put(CommandExecutionStatusVO.SAVED, savedState);
		stateGroup.put(CommandExecutionStatusVO.SENT, sentState);
		stateGroup.put(CommandExecutionStatusVO.DELIVERED, deliveredState);
		stateGroup.put(CommandExecutionStatusVO.COMPLETED, completedState);
		stateGroup.put(CommandExecutionStatusVO.TTL_TIMEOUT, ttlTimeoutState);
		stateGroup.put(CommandExecutionStatusVO.TIMEOUT, timeoutState);
		log.info("状态机配置初始化完成");
	}
	
}
