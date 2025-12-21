package org.pms.domain.command.repository;

import org.pms.domain.command.model.entity.CommandExecutionEntity;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令执行仓储接口
 * @create 2025/12/16
 */
public interface ICommandExecutionRepository {
	
	/**
	 * 创建指令执行记录
	 *
	 * @param commandExecutionEntity 指令执行实体
	 * @return 执行记录ID
	 */
	Long createCommandExecution(CommandExecutionEntity commandExecutionEntity);
	
	/**
	 * 根据aepTaskId和deviceId查询
	 *
	 * @param aepTaskId AEP任务ID
	 * @param deviceId  设备ID
	 * @return 指令执行实体
	 */
	CommandExecutionEntity selectByAepTaskIdAndDeviceId(Long aepTaskId, Long deviceId);
	
	/**
	 * 更新状态
	 *
	 * @param expectedCode           期望状态码
	 * @param targetCode             目标状态码
	 * @param commandExecutionEntity 指令执行实体
	 * @return 是否成功
	 */
	boolean updateStatus(Short expectedCode, Short targetCode, CommandExecutionEntity commandExecutionEntity);
	
	/**
	 * 更新执行结果
	 *
	 * @param commandExecutionEntity 指令执行实体
	 * @return 是否成功
	 */
	boolean updateTrace(CommandExecutionEntity commandExecutionEntity);
	
}

