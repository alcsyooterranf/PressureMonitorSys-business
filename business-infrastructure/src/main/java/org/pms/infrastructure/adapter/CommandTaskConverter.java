package org.pms.infrastructure.adapter;

import org.mapstruct.Mapper;
import org.pms.api.dto.req.CommandTaskCreateReq;
import org.pms.api.dto.resp.CommandTaskQueryView;
import org.pms.domain.command.model.entity.CommandTaskEntity;
import org.pms.infrastructure.mapper.po.CommandTaskPO;

import java.util.List;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令任务转换器
 * @create 2025/12/16
 */
@Mapper(componentModel = "spring")
public abstract class CommandTaskConverter {
	
	/**
	 * PO -> Entity
	 */
	public abstract CommandTaskEntity po2entity(CommandTaskPO commandTaskPO);
	
	/**
	 * PO列表 -> Entity列表
	 */
	public abstract List<CommandTaskEntity> pos2entities(List<CommandTaskPO> commandTaskPOS);
	
	/**
	 * PO -> QueryView
	 */
	public abstract CommandTaskQueryView po2view(CommandTaskPO commandTaskPO);
	
	/**
	 * PO列表 -> QueryView列表
	 */
	public abstract List<CommandTaskQueryView> pos2views(List<CommandTaskPO> commandTaskPOS);
	
	/**
	 * CreateReq -> PO
	 */
	public abstract CommandTaskPO createReq2po(CommandTaskCreateReq commandTaskCreateReq);
	
	/**
	 * Entity -> PO
	 */
	public abstract CommandTaskPO entity2po(CommandTaskEntity commandTaskEntity);
	
}

