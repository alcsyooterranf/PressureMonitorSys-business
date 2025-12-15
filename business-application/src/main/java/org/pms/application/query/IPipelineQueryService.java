package org.pms.application.query;

import org.pms.api.dto.req.PipelineQueryCondition;
import org.pms.api.dto.resp.PipelineQueryView;
import org.pms.domain.terminal.model.entity.PipelineEntity;
import org.pms.api.common.PageResponse;

import java.util.List;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 管道查询仓储接口
 * @create 2025/12/10
 */
public interface IPipelineQueryService {
	
	/**
	 * 按条件查询所有数据, 支持的条件有：管道编码、客户名称、开始时间、结束时间
	 *
	 * @param queryCondition 查询条件
	 * @return 管道视图对象
	 */
	PageResponse<PipelineQueryView> queryPipelineViewPage(PipelineQueryCondition queryCondition);
	
	/**
	 * 根据id列表查询管道信息
	 *
	 * @param ids 管道id列表
	 * @return 管道信息列表
	 */
	List<PipelineEntity> queryByIdList(List<Long> ids);
	
	
}
