package org.pms.application.converter;

import org.pms.api.dto.req.DeviceInsertReq;
import org.pms.api.dto.req.DeviceUpdateReq;
import org.pms.api.dto.req.PipelineInsertReq;
import org.pms.api.dto.req.PipelineUpdateReq;
import org.pms.domain.terminal.model.command.CreateDeviceCommand;
import org.pms.domain.terminal.model.command.CreatePipelineCommand;
import org.pms.domain.terminal.model.command.UpdateDeviceCommand;
import org.pms.domain.terminal.model.command.UpdatePipelineCommand;
import org.springframework.stereotype.Component;

/**
 * 设备/管道模块DTO转换器
 * <p>
 * 职责：将API层的DTO转换为Domain层的Command对象
 * <p>
 * 转换关系：
 * - DeviceInsertReq → CreateDeviceCommand
 * - DeviceUpdateReq → UpdateDeviceCommand
 * - PipelineInsertReq → CreatePipelineCommand
 * - PipelineUpdateReq → UpdatePipelineCommand
 *
 * @author refactor
 * @date 2025-12-18
 */
@Component
public class TerminalDtoConverter {
	
	/**
	 * 转换：API DTO → Domain Command
	 * DeviceInsertReq → CreateDeviceCommand
	 */
	public CreateDeviceCommand toCreateCommand(DeviceInsertReq req) {
		if (req == null) {
			return null;
		}
		
		return CreateDeviceCommand.builder()
				.pipelineId(req.getPipelineId())
				.pipelineSN(req.getPipelineSN())
				.deviceSN(req.getDeviceSN())
				.protocolSN(req.getProtocolSN())
				.templateId(req.getTemplateId())
				.customerAccount(req.getCustomerAccount())
				.temperatureUpperBound(req.getTemperatureUpperBound())
				.temperatureLowerBound(req.getTemperatureLowerBound())
				.voltageUpperBound(req.getVoltageUpperBound())
				.voltageLowerBound(req.getVoltageLowerBound())
				.pressureUpperBound(req.getPressureUpperBound())
				.pressureLowerBound(req.getPressureLowerBound())
				.localAtmosphericPressure(req.getLocalAtmosphericPressure())
				.longitude(req.getLongitude())
				.latitude(req.getLatitude())
				.remark(req.getRemark())
				.build();
	}
	
	/**
	 * 转换：API DTO → Domain Command
	 * DeviceUpdateReq → UpdateDeviceCommand
	 */
	public UpdateDeviceCommand toUpdateCommand(DeviceUpdateReq req) {
		if (req == null) {
			return null;
		}

		return UpdateDeviceCommand.builder()
				.id(req.getId())
				.pipelineId(req.getPipelineId())
				.deviceSN(req.getDeviceSN())
				.status(req.getStatus())
				.templateId(req.getTemplateId())
				.temperatureUpperBound(req.getTemperatureUpperBound())
				.temperatureLowerBound(req.getTemperatureLowerBound())
				.voltageUpperBound(req.getVoltageUpperBound())
				.voltageLowerBound(req.getVoltageLowerBound())
				.pressureUpperBound(req.getPressureUpperBound())
				.pressureLowerBound(req.getPressureLowerBound())
				.localAtmosphericPressure(req.getLocalAtmosphericPressure())
				.location(req.getLocation())
				.longitude(req.getLongitude())
				.latitude(req.getLatitude())
				.remark(req.getRemark())
				.build();
	}
	
	/**
	 * 转换：API DTO → Domain Command
	 * PipelineInsertReq → CreatePipelineCommand
	 */
	public CreatePipelineCommand toCreateCommand(PipelineInsertReq req) {
		if (req == null) {
			return null;
		}
		
		return CreatePipelineCommand.builder()
				.pipelineSN(req.getPipelineSN())
				.pipelineName(req.getPipelineName())
				.customerAccount(req.getCustomerAccount())
				.location(req.getLocation())
				.remark(req.getRemark())
				.build();
	}
	
	/**
	 * 转换：API DTO → Domain Command
	 * PipelineUpdateReq → UpdatePipelineCommand
	 */
	public UpdatePipelineCommand toUpdateCommand(PipelineUpdateReq req) {
		if (req == null) {
			return null;
		}
		
		return UpdatePipelineCommand.builder()
				.id(req.getId())
				.pipelineName(req.getPipelineName())
				.location(req.getLocation())
				.longitude(req.getLongitude())
				.latitude(req.getLatitude())
				.remark(req.getRemark())
				.build();
	}
	
}

