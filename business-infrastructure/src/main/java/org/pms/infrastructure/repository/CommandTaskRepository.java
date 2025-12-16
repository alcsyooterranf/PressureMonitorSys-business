package org.pms.infrastructure.repository;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.dto.req.CommandTaskCreateReq;
import org.pms.domain.command.model.entity.CommandTaskEntity;
import org.pms.domain.command.repository.ICommandTaskRepository;
import org.pms.infrastructure.adapter.CommandTaskConverter;
import org.pms.infrastructure.mapper.ICommandTaskMapper;
import org.pms.infrastructure.mapper.po.CommandTaskPO;
import org.springframework.stereotype.Repository;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令任务仓储实现
 * @create 2025/12/16
 */
@Slf4j
@Repository
public class CommandTaskRepository implements ICommandTaskRepository {
	
	@Resource
	private ICommandTaskMapper commandTaskMapper;
	
	@Resource
	private CommandTaskConverter commandTaskConverter;
	
	@Override
	public Long createCommandTask(CommandTaskCreateReq commandTaskCreateReq) {
		CommandTaskPO commandTaskPO = commandTaskConverter.createReq2po(commandTaskCreateReq);
		int rows = commandTaskMapper.insert(commandTaskPO);
		if (rows > 0) {
			log.info("创建指令任务成功, taskId={}", commandTaskPO.getId());
			return commandTaskPO.getId();
		} else {
			log.error("创建指令任务失败");
			return null;
		}
	}
	
	@Override
	public CommandTaskEntity selectById(Long id) {
		CommandTaskPO commandTaskPO = commandTaskMapper.selectById(id);
		return commandTaskConverter.po2entity(commandTaskPO);
	}
	
}

