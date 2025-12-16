package org.pms.application.query;

import org.pms.api.common.PageResponse;
import org.pms.api.dto.req.CommandMetaQueryCondition;
import org.pms.api.dto.resp.CommandMetaQueryView;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据查询服务接口
 * @create 2025/12/15
 */
public interface ICommandMetaQueryService {
	
	/**
	 * 按条件查询指令元数据（分页）
	 *
	 * @param queryCondition 查询条件
	 * @return 分页响应
	 */
	PageResponse<CommandMetaQueryView> queryCommandMetaPage(CommandMetaQueryCondition queryCondition);
	
}

