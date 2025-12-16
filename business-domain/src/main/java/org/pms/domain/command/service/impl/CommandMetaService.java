package org.pms.domain.command.service.impl;

import com.pms.types.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.dto.req.CommandMetaInsertReq;
import org.pms.api.dto.req.CommandMetaUpdateReq;
import org.pms.domain.command.repository.ICommandMetaRepository;
import org.pms.domain.command.service.CommandMetaValidationService;
import org.pms.domain.command.service.ICommandMetaService;
import org.pms.domain.terminal.repository.IPipelineRepository;
import org.springframework.stereotype.Service;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据服务实现
 * @create 2025/12/15
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
	public void createCommandMeta(CommandMetaInsertReq commandMetaInsertReq) {
		// 1. 校验pipeline_id在t_pipeline表中的存在性
		validatePipelineExists(commandMetaInsertReq.getPipelineId());

		// 2. 校验payload_schema字段
		validationService.validatePayloadSchema(
				commandMetaInsertReq.getPayloadSchema(),
				commandMetaInsertReq.getServiceIdentifier()
		);

		// 3. 执行创建
		commandMetaRepository.createCommandMeta(commandMetaInsertReq);
	}

	@Override
	public void updateCommandMeta(CommandMetaUpdateReq commandMetaUpdateReq) {
		// 1. 校验pipeline_id在t_pipeline表中的存在性
		validatePipelineExists(commandMetaUpdateReq.getPipelineId());

		// 2. 校验payload_schema字段
		validationService.validatePayloadSchema(
				commandMetaUpdateReq.getPayloadSchema(),
				commandMetaUpdateReq.getServiceIdentifier()
		);

		// 3. 执行更新
		commandMetaRepository.updateCommandMeta(commandMetaUpdateReq);
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
			throw new AppException("ERR_BIZ_COMMAND_META_1001", "管道ID不存在");
		}
	}

}

