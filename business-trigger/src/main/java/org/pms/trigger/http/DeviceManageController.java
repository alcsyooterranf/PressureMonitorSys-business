package org.pms.trigger.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.pms.application.converter.TerminalDtoConverter;
import org.pms.application.query.IDeviceQueryService;
import org.pms.api.dto.req.DeviceQueryCondition;
import org.pms.api.dto.resp.DeviceQueryView;
import org.pms.api.dto.req.DeviceInsertReq;
import org.pms.api.dto.req.DeviceUpdateReq;
import org.pms.domain.terminal.model.command.CreateDeviceCommand;
import org.pms.domain.terminal.model.command.UpdateDeviceCommand;
import org.pms.domain.terminal.service.IDeviceService;
import org.pms.api.common.PageResponse;
import org.pms.types.BizCode;
import org.pms.types.BizConstants;
import org.pms.types.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 设备管理控制?
 * <p>
 * 重构说明?
 * - 在Trigger层注入DTO转换?
 * - 将API DTO转换为Domain Command对象
 * - 调用Domain层Service处理业务逻辑
 *
 * @author refactor
 * @date 2025-12-18
 */
@Slf4j
@Validated
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/device_manage/")
public class DeviceManageController {

	private static final String SECURITY_CONTEXT_HEADER = BizConstants.SECURITY_CONTEXT_HEADER;
	@Resource
	private IDeviceService deviceService;
	@Resource
	private IDeviceQueryService deviceQueryService;
	@Resource
	private TerminalDtoConverter terminalDtoConverter;
	
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public Response<PageResponse<DeviceQueryView>> queryDeviceByConditions(@RequestBody DeviceQueryCondition request) {
		PageResponse<DeviceQueryView> deviceViewPage = deviceQueryService.queryDevicePage(request);
		
		return Response.<PageResponse<DeviceQueryView>>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.data(deviceViewPage)
				.build();
	}
	
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public Response<String> deleteDeviceById(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
											 @RequestParam @Min(1) Long id) throws JsonProcessingException {
		deviceService.deleteDeviceById(id, securityContextEncoded);
		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.build();
	}
	
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public Response<String> updateDevice(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
										 @RequestBody @Valid DeviceUpdateReq deviceUpdateReq) throws JsonProcessingException {
		// 1. API DTO -> Domain Command
		UpdateDeviceCommand command = terminalDtoConverter.toUpdateCommand(deviceUpdateReq);

		// 2. 调用Domain层服务
		deviceService.updateDevice(command, securityContextEncoded);

		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.build();

	}

	@RequestMapping(value = "unbind", method = RequestMethod.POST)
	public Response<String> unbindDeviceById(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
											 @RequestParam @Min(1) Long id) throws JsonProcessingException {
		deviceService.unbindDeviceById(id, securityContextEncoded);
		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.build();
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public Response<String> createDevice(@RequestBody @Valid DeviceInsertReq deviceInsertReq) {
		// 1. API DTO -> Domain Command
		CreateDeviceCommand command = terminalDtoConverter.toCreateCommand(deviceInsertReq);

		// 2. 调用Domain层服务
		deviceService.addDevice(command);

		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.build();
	}
	
}
