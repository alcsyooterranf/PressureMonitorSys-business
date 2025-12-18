package org.pms.domain.command.service;

import org.pms.domain.command.model.command.CreateCommandTaskCommand;
import org.pms.domain.command.model.command.SendCommandCommand;

import java.util.concurrent.CompletableFuture;

/**
 * 指令任务服务接口
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author alcsyooterranf
 * @author refactor
 * @date 2025/12/16
 */
public interface ICommandTaskService {

	/**
	 * 创建指令任务
	 * 只定义指令内容，不执行下发
	 *
	 * @param command 创建指令任务命令对象
	 * @return 任务ID
	 */
	Long createCommandTask(CreateCommandTaskCommand command);

	/**
	 * 指令下发（同步）
	 * 执行指令下发并维护状态机
	 *
	 * @param command 发送指令命令对象
	 * @return AEP任务ID
	 */
	Long sendCommand(SendCommandCommand command);

	/**
	 * 指令下发（异步）
	 * 异步执行指令下发并维护状态机
	 *
	 * @param command 发送指令命令对象
	 * @return CompletableFuture<Long> 异步返回AEP任务ID
	 */
	CompletableFuture<Long> sendCommandAsync(SendCommandCommand command);

}

