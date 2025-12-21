package org.pms.domain.command.service.stateflow;

import jakarta.annotation.Resource;
import org.pms.domain.command.model.entity.CommandExecutionEntity;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.domain.command.repository.ICommandExecutionRepository;
import org.pms.types.BizCode;
import org.pms.types.Result;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 抽象状态类, 每个 State 只负责从当前状态出发, 处理某个目标状态
 * @create 2025/12/16
 */
public abstract class AbstractState {
	
	@Resource
	protected ICommandExecutionRepository commandExecutionRepository;
	
	/**
	 * 获取当前状态
	 */
	public abstract CommandExecutionStatusVO current();
	
	/**
	 * 处理状态迁移
	 *
	 * @param ctx    指令状态迁移上下文
	 * @param target 目标状态
	 * @return 执行结果
	 */
	public Result apply(CommandExecutionEntity ctx, CommandExecutionStatusVO target) {
		CommandExecutionStatusVO current = current();
		
		// 1. 终态锁死: 终态不允许迁移（但允许记录 raw/time）
		if (current.isFinalState()) {
			commandExecutionRepository.updateTrace(ctx);
			return Result.buildResult(BizCode.SUCCESS, "已是终态，忽略状态更新，仅记录回调");
		}
		
		// 2. 单调递进：允许跳跃, 但不允许回退
		if (target.getCode() < current.getCode()) {
			commandExecutionRepository.updateTrace(ctx);
			return Result.buildResult(BizCode.SUCCESS, "回调乱序/回退，忽略状态更新，仅记录回调");
		}
		
		// 3. 做一次 CAS 更新（核心！）
		boolean ok = commandExecutionRepository.updateStatus(current.getCode(), target.getCode(), ctx);
		
		return ok
				? Result.buildResult(BizCode.SUCCESS, "状态更新成功：" + current + " -> " + target)
				: Result.buildResult(BizCode.SUCCESS, "状态未更新（可能并发/已变更），已幂等处理");
	}
	
}
