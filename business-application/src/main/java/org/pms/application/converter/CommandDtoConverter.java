package org.pms.application.converter;

import org.pms.api.dto.req.CommandMetaInsertReq;
import org.pms.api.dto.req.CommandMetaUpdateReq;
import org.pms.api.dto.req.CommandTaskCreateReq;
import org.pms.api.dto.req.CommandTaskSendReq;
import org.pms.domain.command.model.command.CreateCommandMetaCommand;
import org.pms.domain.command.model.command.CreateCommandTaskCommand;
import org.pms.domain.command.model.command.SendCommandCommand;
import org.pms.domain.command.model.command.UpdateCommandMetaCommand;
import org.springframework.stereotype.Component;

/**
 * 指令模块DTO转换器
 * <p>
 * 职责：将API层的DTO转换为Domain层的Command对象
 * <p>
 * 转换关系：
 * - CommandMetaInsertReq → CreateCommandMetaCommand
 * - CommandMetaUpdateReq → UpdateCommandMetaCommand
 * - CommandTaskCreateReq → CreateCommandTaskCommand
 * - CommandTaskSendReq → SendCommandCommand
 *
 * @author refactor
 * @date 2025-12-18
 */
@Component
public class CommandDtoConverter {
	
	/**
	 * 转换：API DTO → Domain Command
	 * CommandMetaInsertReq → CreateCommandMetaCommand
	 */
	public CreateCommandMetaCommand toCreateCommand(CommandMetaInsertReq req) {
		if (req == null) {
			return null;
		}
		
		return CreateCommandMetaCommand.builder()
				.pipelineId(req.getPipelineId())
				.serviceIdentifier(req.getServiceIdentifier())
				.name(req.getName())
				.payloadSchema(req.getPayloadSchema())
				.remark(req.getRemark())
				.build();
	}
	
	/**
	 * 转换：API DTO → Domain Command
	 * CommandMetaUpdateReq → UpdateCommandMetaCommand
	 */
	public UpdateCommandMetaCommand toUpdateCommand(CommandMetaUpdateReq req) {
		if (req == null) {
			return null;
		}
		
		return UpdateCommandMetaCommand.builder()
				.id(req.getId())
				.pipelineId(req.getPipelineId())
				.serviceIdentifier(req.getServiceIdentifier())
				.name(req.getName())
				.payloadSchema(req.getPayloadSchema())
				.remark(req.getRemark())
				.build();
	}
	
	/**
	 * 转换：API DTO → Domain Command
	 * CommandTaskCreateReq → CreateCommandTaskCommand
	 */
	public CreateCommandTaskCommand toCreateTaskCommand(CommandTaskCreateReq req) {
		if (req == null) {
			return null;
		}

		return CreateCommandTaskCommand.builder()
				.tenantId(req.getTenantId())
				.pipelineId(req.getPipelineId())
				.serviceIdentifier(req.getServiceIdentifier())
				.name(req.getName())
				.args(req.getArgs())
				.createBy(req.getCreateBy())
				.build();
	}
	
	/**
	 * 转换：API DTO → Domain Command
	 * CommandTaskSendReq → SendCommandCommand
	 */
	public SendCommandCommand toSendCommand(CommandTaskSendReq req) {
		if (req == null) {
			return null;
		}
		
		return SendCommandCommand.builder()
				.taskId(req.getTaskId())
				.deviceId(req.getDeviceId())
				.deviceSN(req.getDeviceSN())
				.pipelineId(req.getPipelineId())
				.build();
	}
	
}

