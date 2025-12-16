package org.pms.domain.command.service;

import org.pms.api.dto.req.CommandMetaInsertReq;
import org.pms.api.dto.req.CommandMetaUpdateReq;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据服务接口
 * @create 2025/12/15
 */
public interface ICommandMetaService {
	
	/**
	 * 创建指令元数据
	 * 如果已存在同pipeline_id + service_identifier的记录，则执行更新操作并使version + 1
	 *
	 * @param commandMetaInsertReq 指令元数据创建请求
	 */
	void createCommandMeta(CommandMetaInsertReq commandMetaInsertReq);
	
	/**
	 * 更新指令元数据
	 *
	 * @param commandMetaUpdateReq 指令元数据更新请求
	 */
	void updateCommandMeta(CommandMetaUpdateReq commandMetaUpdateReq);
	
	/**
	 * 废弃指令元数据（将status改为3-DEPRECATED）
	 *
	 * @param id 主键ID
	 */
	void deprecateCommandMeta(Long id);
	
	/**
	 * 验证指令元数据（将status从1-UNVERIFIED变为2-VERIFIED）
	 *
	 * @param id 主键ID
	 */
	void verifyCommandMeta(Long id);
	
}

