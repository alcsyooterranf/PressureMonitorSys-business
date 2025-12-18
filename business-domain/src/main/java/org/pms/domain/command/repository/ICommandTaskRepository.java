package org.pms.domain.command.repository;

import org.pms.domain.command.model.command.CreateCommandTaskCommand;
import org.pms.domain.command.model.entity.CommandTaskEntity;

/**
 * 指令任务仓储接口
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author alcsyooterranf
 * @author refactor
 * @date 2025/12/16
 */
public interface ICommandTaskRepository {

	/**
	 * 创建指令任务
	 *
	 * @param command 创建指令任务命令对象
	 * @return 任务ID
	 */
	Long createCommandTask(CreateCommandTaskCommand command);

	/**
	 * 根据ID查询
	 *
	 * @param id 主键ID
	 * @return 指令任务实体
	 */
	CommandTaskEntity selectById(Long id);

}

