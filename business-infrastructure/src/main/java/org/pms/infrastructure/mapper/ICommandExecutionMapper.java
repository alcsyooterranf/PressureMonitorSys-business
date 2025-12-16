package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
	CommandExecutionPO selectByAepTaskIdAndDeviceId(@Param("aepTaskId") Long aepTaskId, @Param("deviceId") Long deviceId);
	
	/**
	 * 更新状态
	 *
	 * @param aepTaskId AEP任务ID
	 * @param deviceId  设备ID
	 * @param status    状态
	 * @return 影响行数
	 */
	int updateStatus(@Param("aepTaskId") Long aepTaskId, @Param("deviceId") Long deviceId, @Param("status") Short status);
	
	/**
	 * 更新执行结果
	 *
	 * @param commandExecutionPO 指令执行PO
	 * @return 影响行数
	 */
	int updateResult(CommandExecutionPO commandExecutionPO);
	
}

