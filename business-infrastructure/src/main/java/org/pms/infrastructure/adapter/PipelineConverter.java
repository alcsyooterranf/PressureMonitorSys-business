package org.pms.infrastructure.adapter;

import org.mapstruct.Mapper;
import org.pms.api.dto.resp.PipelineQueryView;
import org.pms.domain.terminal.model.entity.PipelineEntity;
import org.pms.api.dto.req.PipelineInsertReq;
import org.pms.infrastructure.mapper.po.PipelinePO;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PipelineConverter {
	
	/**
	 * 单个JavaBean转换 ProductPO -> ProductEntity
	 *
	 * @param pipelinePO PO类
	 * @return Entity类
	 */
	public abstract PipelineEntity po2entity(PipelinePO pipelinePO);
	
	/**
	 * 批量转换 List<ProductPO> -> List<ProductEntity>
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
	 * 单个JavaBean转换 ProductInsertReq -> ProductPO
	 *
	 * @param pipelineInsertReq insertReq类
	 * @return PO类
	 */
	public abstract PipelinePO insertReq2PO(PipelineInsertReq pipelineInsertReq);
	
}
