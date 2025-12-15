package org.pms.domain.terminal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pms.api.dto.req.PipelineInsertReq;
import org.pms.api.dto.req.PipelineUpdateReq;

public interface IPipelineService {
	
	/**
	 * 逻辑删除管道
	 *
	 * @param id 管道id
	 */
	void deletePipelineById(Long id, String securityContextEncoded) throws JsonProcessingException;
	
	/**
	 * 根据管道ID更新管道信息
	 *
	 * @param pipelineUpdateReq 管道更新请求
	 */
	void updatePipeline(PipelineUpdateReq pipelineUpdateReq, String securityContextEncoded) throws JsonProcessingException;
	
	/**
	 * 新增管道
	 *
	 * @param pipelineInsertReq 新增管道请求
	 */
	void addPipeline(PipelineInsertReq pipelineInsertReq);
	
}
