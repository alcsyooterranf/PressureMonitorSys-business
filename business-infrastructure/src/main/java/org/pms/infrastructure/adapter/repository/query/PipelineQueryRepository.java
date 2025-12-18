package org.pms.infrastructure.adapter.repository.query;

import jakarta.annotation.Resource;
import org.pms.application.query.IPipelineQueryService;
import org.pms.api.dto.req.PipelineQueryCondition;
import org.pms.api.dto.resp.PipelineQueryView;
import org.pms.domain.terminal.model.entity.PipelineEntity;
import org.pms.infrastructure.adapter.converter.PipelineConverter;
import org.pms.infrastructure.mapper.IPipelineMapper;
import org.pms.infrastructure.mapper.po.PipelinePO;
import org.pms.api.common.PageResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 管道查询仓储服务
 * @create 2025/12/10
 */
@Repository
public class PipelineQueryRepository implements IPipelineQueryService {
	
	@Resource
	private PipelineConverter pipelineConverter;
	@Resource
	private IPipelineMapper pipelineMapper;
	
	@Override
	public PageResponse<PipelineQueryView> queryPipelineViewPage(PipelineQueryCondition queryCondition) {
		Long count = pipelineMapper.queryPipelineCount(queryCondition);
		List<PipelinePO> pipelinePOS = pipelineMapper.queryPipelineList(queryCondition);
		
		// 空值提前返回
		if (Objects.equals(count, 0L) || pipelinePOS.isEmpty()) {
			return PageResponse.<PipelineQueryView>builder().count(count).build();
		}
		
		// 对象转换
		List<PipelineQueryView> pipelineQueryViews = pipelineConverter.pos2views(pipelinePOS);
		
		return PageResponse.<PipelineQueryView>builder().count(count).data(pipelineQueryViews).build();
	}
	
	@Override
	public List<PipelineEntity> queryByIdList(List<Long> ids) {
		return pipelineConverter.pos2entities(pipelineMapper.queryByIdList(ids));
	}
	
}
