package org.pms.domain.command.repository;

import org.pms.api.dto.req.CommandTaskCreateReq;
import org.pms.domain.command.model.entity.CommandTaskEntity;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令任务仓储接口
 * @create 2025/12/16
 */
public interface ICommandTaskRepository {
	
	/**
	 * 创建指令任务
	 *
	 * @param commandTaskCreateReq 指令任务创建请求
	 * @return 任务ID
	 */
	Long createCommandTask(CommandTaskCreateReq commandTaskCreateReq);
	
	/**
	 * 根据ID查询
	 *
	 * @param id 主键ID
	 * @return 指令任务实体
	 */
	CommandTaskEntity selectById(Long id);
	
}

