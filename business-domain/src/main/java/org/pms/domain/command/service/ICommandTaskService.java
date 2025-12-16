package org.pms.domain.command.service;

import org.pms.api.dto.req.CommandTaskCreateReq;
import org.pms.api.dto.req.CommandTaskSendReq;

import java.util.concurrent.CompletableFuture;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令任务服务接口
 * @create 2025/12/16
 */
public interface ICommandTaskService {

	/**
	 * 创建指令任务
	 * 只定义指令内容，不执行下发
	 *
	 * @param commandTaskCreateReq 指令任务创建请求
	 * @return 任务ID
	 */
	Long createCommandTask(CommandTaskCreateReq commandTaskCreateReq);

	/**
	 * 指令下发（同步）
	 * 执行指令下发并维护状态机
	 *
	 * @param commandTaskSendReq 指令下发请求
	 * @return AEP任务ID
	 */
	Long sendCommand(CommandTaskSendReq commandTaskSendReq);

	/**
	 * 指令下发（异步）
	 * 异步执行指令下发并维护状态机
	 *
	 * @param commandTaskSendReq 指令下发请求
	 * @return CompletableFuture<Long> 异步返回AEP任务ID
	 */
	CompletableFuture<Long> sendCommandAsync(CommandTaskSendReq commandTaskSendReq);

}

