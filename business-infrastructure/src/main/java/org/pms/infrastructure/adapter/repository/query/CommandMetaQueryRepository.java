package org.pms.infrastructure.adapter.repository.query;

import jakarta.annotation.Resource;
import org.pms.api.common.PageResponse;
import org.pms.api.dto.req.CommandMetaQueryCondition;
import org.pms.api.dto.resp.CommandMetaQueryView;
import org.pms.application.query.ICommandMetaQueryService;
import org.pms.infrastructure.adapter.converter.CommandMetaConverter;
import org.pms.infrastructure.mapper.ICommandMetaMapper;
import org.pms.infrastructure.mapper.po.CommandMetaPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据查询仓储服务
 * @create 2025/12/15
 */
@Repository
public class CommandMetaQueryRepository implements ICommandMetaQueryService {
	
	@Resource
	private CommandMetaConverter commandMetaConverter;
	@Resource
	private ICommandMetaMapper commandMetaMapper;
	
	@Override
	public PageResponse<CommandMetaQueryView> queryCommandMetaPage(CommandMetaQueryCondition queryCondition) {
		// 分页查询
		Long count = commandMetaMapper.countByCondition(queryCondition);
		List<CommandMetaPO> commandMetaPOS = commandMetaMapper.selectByCondition(queryCondition);
		
		// 空值提前返回
		if (Objects.equals(count, 0L) || commandMetaPOS.isEmpty()) {
			return PageResponse.<CommandMetaQueryView>builder().count(count).build();
		}
		
		// 对象转换
		List<CommandMetaQueryView> commandMetaQueryViews = commandMetaConverter.pos2views(commandMetaPOS);
		
		return PageResponse.<CommandMetaQueryView>builder().count(count).data(commandMetaQueryViews).build();
	}
	
}

