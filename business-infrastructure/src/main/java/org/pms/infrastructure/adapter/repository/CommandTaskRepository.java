package org.pms.infrastructure.adapter.repository;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.command.model.command.CreateCommandTaskCommand;
import org.pms.domain.command.model.entity.CommandTaskEntity;
import org.pms.domain.command.repository.ICommandTaskRepository;
import org.pms.infrastructure.adapter.converter.CommandTaskConverter;
import org.pms.infrastructure.mapper.ICommandTaskMapper;
import org.pms.infrastructure.mapper.po.CommandTaskPO;
import org.springframework.stereotype.Repository;

/**
 * 指令任务仓储实现
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author alcsyooterranf
 * @author refactor
 * @date 2025/12/16
 */
@Slf4j
@Repository
public class CommandTaskRepository implements ICommandTaskRepository {

	@Resource
	private ICommandTaskMapper commandTaskMapper;

	@Resource
	private CommandTaskConverter commandTaskConverter;

	@Override
	public Long createCommandTask(CreateCommandTaskCommand command) {
		CommandTaskPO commandTaskPO = commandTaskConverter.command2po(command);
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

