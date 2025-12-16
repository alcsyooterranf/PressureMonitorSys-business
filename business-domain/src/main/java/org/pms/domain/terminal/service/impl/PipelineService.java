package org.pms.domain.terminal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.rbac.model.req.SecurityContextHeader;
import org.pms.api.dto.req.PipelineInsertReq;
import org.pms.api.dto.req.PipelineUpdateReq;
import org.pms.domain.terminal.repository.IPipelineRepository;
import org.pms.domain.terminal.service.IPipelineService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PipelineService implements IPipelineService {
	
	@Resource
	private IPipelineRepository pipelineRepository;
	
	@Override
	public void deletePipelineById(Long id, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		pipelineRepository.deletePipelineById(id, securityContext.getUsername());
	}
	
	@Override
	public void updatePipeline(PipelineUpdateReq pipelineUpdateReq, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		pipelineRepository.updateProduct(pipelineUpdateReq, securityContextEncoded);
	}
	
	@Override
	public void addPipeline(PipelineInsertReq pipelineInsertReq) {
		pipelineRepository.insertProduct(pipelineInsertReq);
	}
	
}
