package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pms.infrastructure.mapper.po.CommandTaskPO;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令任务Mapper接口
 * @create 2025/12/16
 */
@Mapper
public interface ICommandTaskMapper {
	
	/**
	 * 插入指令任务
	 *
	 * @param commandTaskPO 指令任务PO
	 * @return 影响行数
	 */
	int insert(CommandTaskPO commandTaskPO);
	
	/**
	 * 根据ID查询
	 *
	 * @param id 主键ID
	 * @return 指令任务PO
	 */
	CommandTaskPO selectById(@Param("id") Long id);
	
}

