package org.pms.infrastructure.adapter.converter;

import org.mapstruct.Mapper;
import org.pms.api.dto.resp.CommandTaskQueryView;
import org.pms.domain.command.model.command.CreateCommandTaskCommand;
import org.pms.domain.command.model.entity.CommandTaskEntity;
import org.pms.infrastructure.mapper.po.CommandTaskPO;

import java.util.List;

/**
 * 指令任务转换器
 * <p>
 * 重构说明：
 * - 移除API DTO转换方法（createReq2po）
 * - 新增Command对象转换方法（command2po）
 * - 只保留Entity↔PO和PO→View的转换
 *
 * @author alcsyooterranf
 * @author refactor
 * @date 2025/12/16
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
	 * CreateCommandTaskCommand -> PO
	 */
	public abstract CommandTaskPO command2po(CreateCommandTaskCommand command);

	/**
	 * Entity -> PO
	 */
	public abstract CommandTaskPO entity2po(CommandTaskEntity commandTaskEntity);

}

