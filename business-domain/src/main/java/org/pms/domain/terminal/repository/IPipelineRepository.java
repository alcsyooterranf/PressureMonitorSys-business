package org.pms.domain.terminal.repository;

import org.pms.api.dto.req.PipelineInsertReq;
import org.pms.api.dto.req.PipelineUpdateReq;

public interface IPipelineRepository {
	
	/**
	 * 逻辑删除产品
	 *
	 * @param id 产品id
	 */
	void deleteProductById(Long id, String operatorName);
	
	/**
	 * 根据产品ID更新产品信息
	 *
	 * @param pipelineUpdateReq 产品更新请求
	 */
	void updateProduct(PipelineUpdateReq pipelineUpdateReq, String operatorName);
	
	/**
	 * 新增产品
	 *
	 * @param pipelineInsertReq 新增产品请求
	 */
	void insertProduct(PipelineInsertReq pipelineInsertReq);
	
}
