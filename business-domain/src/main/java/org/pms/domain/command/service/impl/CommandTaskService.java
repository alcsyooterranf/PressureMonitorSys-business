package org.pms.domain.command.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.command.model.command.CreateCommandTaskCommand;
import org.pms.domain.command.model.command.SendCommandCommand;
import org.pms.domain.command.model.entity.CommandExecutionEntity;
import org.pms.domain.command.model.entity.CommandTaskEntity;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.domain.command.repository.ICommandExecutionRepository;
import org.pms.domain.command.repository.ICommandMetaRepository;
import org.pms.domain.command.repository.ICommandTaskRepository;
import org.pms.domain.command.service.AepCommandClient;
import org.pms.domain.command.service.CommandMetaValidationService;
import org.pms.domain.command.service.ICommandTaskService;
import org.pms.types.BizCode;
import org.pms.types.BizException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * 指令任务服务实现
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author alcsyooterranf
 * @author refactor
 * @date 2025/12/16
 */
@Slf4j
@Service
public class CommandTaskService implements ICommandTaskService {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	@Resource
	private ICommandTaskRepository commandTaskRepository;
	@Resource
	private ICommandExecutionRepository commandExecutionRepository;
	@Resource
	private ICommandMetaRepository commandMetaRepository;
	@Resource
	private CommandMetaValidationService validationService;
	@Resource
	private AepCommandClient aepCommandClient;
	
	@Override
	public Long createCommandTask(CreateCommandTaskCommand command) {
		// 1. 校验(pipelineId, serviceIdentifier)在t_command_meta中的存在性
		String payloadSchema = commandMetaRepository.getPayloadSchemaByPipelineAndService(
				command.getPipelineId(),
				command.getServiceIdentifier()
		);

		if (payloadSchema == null) {
			log.error("指令元数据不存在, pipelineId={}, serviceIdentifier={}",
					command.getPipelineId(), command.getServiceIdentifier());
			throw new BizException(BizCode.COMMAND_META_NOT_EXIST, "指令元数据不存在: pipelineId=" + command.getPipelineId() +
					", serviceIdentifier=" + command.getServiceIdentifier());
		}

		// 2. 按照payload_schema对args做参数校验
		try {
			JsonNode schemaNode = OBJECT_MAPPER.readTree(payloadSchema);
			JsonNode inputSchemaNode = schemaNode.get("inputSchema");
			String inputSchemaJson = OBJECT_MAPPER.writeValueAsString(inputSchemaNode);

			validationService.validateArgs(command.getArgs(), inputSchemaJson);

		} catch (BizException e) {
			throw e;
		} catch (Exception e) {
			log.error("解析payload_schema异常", e);
			throw new BizException(BizCode.COMMAND_META_PAYLOAD_SCHEMA_PARSE_ERROR, "解析payload_schema异常: " + e.getMessage());
		}

		// 3. 创建指令任务
		Long taskId = commandTaskRepository.createCommandTask(command);
		if (taskId == null) {
			log.error("创建指令任务失败");
			throw new BizException(BizCode.COMMAND_TASK_CREATE_FAILED);
		}

		log.info("创建指令任务成功, taskId={}", taskId);
		return taskId;
	}
	
	@Override
	public Long sendCommand(SendCommandCommand command) {
		// 1. 查询指令任务
		CommandTaskEntity commandTask = commandTaskRepository.selectById(command.getTaskId());
		if (commandTask == null) {
			log.error("指令任务不存在, taskId={}", command.getTaskId());
			throw new BizException(BizCode.COMMAND_TASK_NOT_EXIST);
		}

		// 2. 解析args为JSONObject
		JSONObject params = JSON.parseObject(commandTask.getArgs());

		// 3. 调用AEP SDK发送指令（同步）
		Long aepTaskId = aepCommandClient.sendCommand(
				command.getDeviceSN(),
				command.getPipelineId(),
				commandTask.getServiceIdentifier(),
				params
		);

		// 4. 创建指令执行记录（初始状态为INITIALIZED）
		CommandExecutionEntity commandExecution = CommandExecutionEntity.builder()
				.commandTaskId(commandTask.getId())
				.tenantId(commandTask.getTenantId())
				.pipelineId(commandTask.getPipelineId())
				.deviceId(command.getDeviceId())
				.deviceSN(command.getDeviceSN())
				.serviceIdentifier(commandTask.getServiceIdentifier())
				.aepTaskId(aepTaskId)
				.status(CommandExecutionStatusVO.SAVED)
				.requestPayload(commandTask.getArgs())
				.build();

		Long executionId = commandExecutionRepository.createCommandExecution(commandExecution);
		if (executionId == null) {
			log.error("创建指令执行记录失败");
			throw new BizException(BizCode.COMMAND_EXECUTION_CREATE_FAILED);
		}

		log.info("指令下发成功, taskId={}, aepTaskId={}, executionId={}",
				commandTask.getId(), aepTaskId, executionId);

		return aepTaskId;
	}
	
	@Override
	public CompletableFuture<Long> sendCommandAsync(SendCommandCommand command) {
		// 1. 查询指令任务
		CommandTaskEntity commandTask = commandTaskRepository.selectById(command.getTaskId());
		if (commandTask == null) {
			log.error("指令任务不存在, taskId={}", command.getTaskId());
			throw new BizException(BizCode.COMMAND_TASK_NOT_EXIST);
		}

		// 2. 解析args为JSONObject
		JSONObject params = JSON.parseObject(commandTask.getArgs());

		// 3. 调用AEP SDK异步发送指令
		log.info("开始异步发送指令, taskId={}, deviceSN={}, pipelineId={}",
				commandTask.getId(), command.getDeviceSN(), command.getPipelineId());

		return aepCommandClient.sendCommandAsync(
				command.getDeviceSN(),
				command.getPipelineId(),
				commandTask.getServiceIdentifier(),
				params
		).thenApply(aepTaskId -> {
			// 4. 创建指令执行记录（初始状态为INITIALIZED）
			log.info("异步指令下发成功，开始创建执行记录, aepTaskId={}", aepTaskId);

			CommandExecutionEntity commandExecution = CommandExecutionEntity.builder()
					.commandTaskId(commandTask.getId())
					.tenantId(commandTask.getTenantId())
					.pipelineId(commandTask.getPipelineId())
					.deviceId(command.getDeviceId())
					.serviceIdentifier(commandTask.getServiceIdentifier())
					.aepTaskId(aepTaskId)
					.status(CommandExecutionStatusVO.SAVED)
					.requestPayload(commandTask.getArgs())
					.build();

			Long executionId = commandExecutionRepository.createCommandExecution(commandExecution);
			if (executionId == null) {
				log.error("创建指令执行记录失败");
				throw new BizException(BizCode.COMMAND_EXECUTION_CREATE_FAILED);
			}

			log.info("异步指令下发完成, taskId={}, aepTaskId={}, executionId={}",
					commandTask.getId(), aepTaskId, executionId);

			return aepTaskId;
		}).exceptionally(throwable -> {
			log.error("异步指令下发失败, taskId={}", commandTask.getId(), throwable);
			throw new BizException(BizCode.COMMAND_ASYNC_SEND_FAILED, "异步指令下发失败: " + throwable.getMessage());
		});
	}
	
}

