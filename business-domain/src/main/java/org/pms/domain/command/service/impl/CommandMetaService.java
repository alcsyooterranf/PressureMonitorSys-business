package org.pms.domain.command.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.command.model.command.CreateCommandMetaCommand;
import org.pms.domain.command.model.command.UpdateCommandMetaCommand;
import org.pms.domain.command.repository.ICommandMetaRepository;
import org.pms.domain.command.service.CommandMetaValidationService;
import org.pms.domain.command.service.ICommandMetaService;
import org.pms.domain.terminal.repository.IPipelineRepository;
import org.pms.types.BizCode;
import org.pms.types.exception.BizException;
import org.springframework.stereotype.Service;

/**
 * 指令元数据服务实现
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author alcsyooterranf
 * @author refactor
 * @date 2025/12/15
 */
@Slf4j
@Service
public class CommandMetaService implements ICommandMetaService {

	@Resource
	private ICommandMetaRepository commandMetaRepository;
	@Resource
	private CommandMetaValidationService validationService;
	@Resource
	private IPipelineRepository pipelineRepository;

	@Override
	public void createCommandMeta(CreateCommandMetaCommand command) {
		// 1. 校验pipeline_id在t_pipeline表中的存在性
		validatePipelineExists(command.getPipelineId());

		// 2. 校验payload_schema字段
		validationService.validatePayloadSchema(
				command.getPayloadSchema(),
				command.getServiceIdentifier()
		);

		// 3. 执行创建
		commandMetaRepository.createCommandMeta(command);
	}

	@Override
	public void updateCommandMeta(UpdateCommandMetaCommand command) {
		// 1. 校验pipeline_id在t_pipeline表中的存在性
		validatePipelineExists(command.getPipelineId());

		// 2. 校验payload_schema字段
		validationService.validatePayloadSchema(
				command.getPayloadSchema(),
				command.getServiceIdentifier()
		);

		// 3. 执行更新
		commandMetaRepository.updateCommandMeta(command);
	}

	@Override
	public void deprecateCommandMeta(Long id) {
		commandMetaRepository.deprecateCommandMeta(id);
	}

	@Override
	public void verifyCommandMeta(Long id) {
		commandMetaRepository.verifyCommandMeta(id);
	}

	/**
	 * 校验pipeline_id是否存在
	 *
	 * @param pipelineId 管道ID
	 */
	private void validatePipelineExists(Long pipelineId) {
		if (!pipelineRepository.isPipelineExist(pipelineId)) {
			throw new BizException(BizCode.COMMAND_META_PIPELINE_NOT_EXIST);
		}
	}

}

