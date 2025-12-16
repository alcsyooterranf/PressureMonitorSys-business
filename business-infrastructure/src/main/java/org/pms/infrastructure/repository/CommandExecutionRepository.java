package org.pms.infrastructure.repository;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.command.model.entity.CommandExecutionEntity;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.domain.command.repository.ICommandExecutionRepository;
import org.pms.infrastructure.adapter.CommandExecutionConverter;
import org.pms.infrastructure.mapper.ICommandExecutionMapper;
import org.pms.infrastructure.mapper.po.CommandExecutionPO;
import org.springframework.stereotype.Repository;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令执行仓储实现
 * @create 2025/12/16
 */
@Slf4j
@Repository
public class CommandExecutionRepository implements ICommandExecutionRepository {
	
	@Resource
	private ICommandExecutionMapper commandExecutionMapper;
	
	@Resource
	private CommandExecutionConverter commandExecutionConverter;
	
	@Override
	public Long createCommandExecution(CommandExecutionEntity commandExecutionEntity) {
		CommandExecutionPO commandExecutionPO = commandExecutionConverter.entity2po(commandExecutionEntity);
		int rows = commandExecutionMapper.insert(commandExecutionPO);
		if (rows > 0) {
			log.info("创建指令执行记录成功, executionId={}", commandExecutionPO.getId());
			return commandExecutionPO.getId();
		} else {
			log.error("创建指令执行记录失败");
			return null;
		}
	}
	
	@Override
	public CommandExecutionEntity selectByAepTaskIdAndDeviceId(Long aepTaskId, Long deviceId) {
		CommandExecutionPO commandExecutionPO = commandExecutionMapper.selectByAepTaskIdAndDeviceId(aepTaskId, deviceId);
		return commandExecutionConverter.po2entity(commandExecutionPO);
	}
	
	@Override
	public boolean updateStatus(Long aepTaskId, Long deviceId, CommandExecutionStatusVO status) {
		int rows = commandExecutionMapper.updateStatus(aepTaskId, deviceId, status.getCode());
		if (rows > 0) {
			log.info("更新指令状态成功, aepTaskId={}, deviceId={}, status={}", aepTaskId, deviceId, status);
			return true;
		} else {
			log.error("更新指令状态失败, aepTaskId={}, deviceId={}, status={}", aepTaskId, deviceId, status);
			return false;
		}
	}
	
	@Override
	public boolean updateResult(CommandExecutionEntity commandExecutionEntity) {
		CommandExecutionPO commandExecutionPO = commandExecutionConverter.entity2po(commandExecutionEntity);
		int rows = commandExecutionMapper.updateResult(commandExecutionPO);
		if (rows > 0) {
			log.info("更新指令执行结果成功, aepTaskId={}, deviceId={}", 
					commandExecutionPO.getAepTaskId(), commandExecutionPO.getDeviceId());
			return true;
		} else {
			log.error("更新指令执行结果失败, aepTaskId={}, deviceId={}", 
					commandExecutionPO.getAepTaskId(), commandExecutionPO.getDeviceId());
			return false;
		}
	}
	
}

