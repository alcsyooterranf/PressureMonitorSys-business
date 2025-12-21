package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.pms.infrastructure.mapper.po.CommandExecutionPO;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令执行Mapper接口
 * @create 2025/12/16
 */
@Mapper
public interface ICommandExecutionMapper {
	
	/**
	 * 插入指令执行记录
	 *
	 * @param commandExecutionPO 指令执行PO
	 * @return 影响行数
	 */
	int insert(CommandExecutionPO commandExecutionPO);
	
	/**
	 * 根据aepTaskId和deviceId查询
	 *
	 * @param aepTaskId AEP任务ID
	 * @param deviceId  设备ID
	 * @return 指令执行PO
	 */
	CommandExecutionPO selectByAepTaskIdAndDeviceId(Long aepTaskId, Long deviceId);
	
	/**
	 * 更新状态
	 *
	 * @param expectedCode       期望状态码
	 * @param targetCode         目标状态码
	 * @param commandExecutionPO 指令执行PO
	 * @return 影响行数
	 */
	int updateStatus(Short expectedCode, Short targetCode, CommandExecutionPO commandExecutionPO);
	
	/**
	 * 更新trace
	 *
	 * @param commandExecutionPO 指令执行PO
	 * @return 影响行数
	 */
	int updateTrace(CommandExecutionPO commandExecutionPO);
	
}

