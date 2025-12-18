package org.pms.infrastructure.adapter.converter;

import org.mapstruct.Mapper;
import org.pms.api.dto.resp.PipelineQueryView;
import org.pms.domain.terminal.model.command.CreatePipelineCommand;
import org.pms.domain.terminal.model.entity.PipelineEntity;
import org.pms.infrastructure.mapper.po.PipelinePO;

import java.util.List;

/**
 * 管道转换器
 * <p>
 * 重构说明：
 * - 移除API DTO转换方法（insertReq2PO）
 * - 新增Command对象转换方法（command2PO）
 * - 只保留Entity↔PO和PO→View的转换
 *
 * @author refactor
 * @date 2025-12-18
 */
@Mapper(componentModel = "spring")
public abstract class PipelineConverter {

	/**
	 * 单个JavaBean转换 PipelinePO -> PipelineEntity
	 *
	 * @param pipelinePO PO类
	 * @return Entity类
	 */
	public abstract PipelineEntity po2entity(PipelinePO pipelinePO);

	/**
	 * 批量转换 List<PipelinePO> -> List<PipelineEntity>
	 *
	 * @param pipelinePOS PO类列表
	 * @return Entity类列表
	 */
	public abstract List<PipelineEntity> pos2entities(List<PipelinePO> pipelinePOS);

	/**
	 * 单个JavaBean转换 PipelinePO -> PipelineQueryView
	 *
	 * @param pipelinePO PO
	 * @return PipelineQueryView
	 */
	public abstract PipelineQueryView po2view(PipelinePO pipelinePO);

	/**
	 * 批量转换 List<PipelinePO> -> List<PipelineQueryView>
	 *
	 * @param pipelinePOS PO列表
	 * @return List<PipelineQueryView>
	 */
	public abstract List<PipelineQueryView> pos2views(List<PipelinePO> pipelinePOS);

	/**
	 * 单个JavaBean转换 CreatePipelineCommand -> PipelinePO
	 *
	 * @param command 创建管道命令对象
	 * @return PO类
	 */
	public abstract PipelinePO command2PO(CreatePipelineCommand command);

}
