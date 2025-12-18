package org.pms.domain.terminal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.rbac.model.req.SecurityContextHeader;
import org.pms.domain.terminal.model.command.CreatePipelineCommand;
import org.pms.domain.terminal.model.command.UpdatePipelineCommand;
import org.pms.domain.terminal.repository.IPipelineRepository;
import org.pms.domain.terminal.service.IPipelineService;
import org.springframework.stereotype.Service;

/**
 * 管道服务实现
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author refactor
 * @date 2025-12-18
 */
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
	public void updatePipeline(UpdatePipelineCommand command, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		pipelineRepository.updatePipeline(command, securityContext.getUsername());
	}

	@Override
	public void addPipeline(CreatePipelineCommand command) {
		pipelineRepository.insertPipeline(command);
	}

}
