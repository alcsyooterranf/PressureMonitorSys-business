package org.pms.trigger.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pms.types.Constants;
import com.pms.types.ResponseCode;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.common.HttpResponse;
import org.pms.application.query.IDeviceQueryService;
import org.pms.api.dto.req.DeviceQueryCondition;
import org.pms.api.dto.resp.DeviceQueryView;
import org.pms.api.dto.req.DeviceInsertReq;
import org.pms.api.dto.req.DeviceUpdateReq;
import org.pms.domain.terminal.service.IDeviceService;
import org.pms.api.common.PageResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/device_manage/")
public class DeviceManageController {
	
	private static final String SECURITY_CONTEXT_HEADER = Constants.SECURITY_CONTEXT_HEADER;
	@Resource
	private IDeviceService deviceService;
	@Resource
	private IDeviceQueryService deviceQueryService;
	
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public HttpResponse<PageResponse<DeviceQueryView>> queryDeviceByConditions(@RequestBody DeviceQueryCondition request) {
		PageResponse<DeviceQueryView> deviceViewPage = deviceQueryService.queryDevicePage(request);
		
		return HttpResponse.<PageResponse<DeviceQueryView>>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.data(deviceViewPage)
				.build();
	}
	
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public HttpResponse<String> deleteDeviceById(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
												 @RequestParam @Min(1) Long id) throws JsonProcessingException {
		deviceService.deleteDeviceById(id, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public HttpResponse<String> updateDevice(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
											 @RequestBody @Valid DeviceUpdateReq deviceUpdateReq) throws JsonProcessingException {
		deviceService.updateDevice(deviceUpdateReq, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
		
	}
	
	@RequestMapping(value = "unbind", method = RequestMethod.POST)
	public HttpResponse<String> unbindDeviceById(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
												 @RequestParam @Min(1) Long id) throws JsonProcessingException {
		deviceService.unbindDeviceById(id, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public HttpResponse<String> createDevice(@RequestBody @Valid DeviceInsertReq deviceInsertReq) {
		deviceService.addDevice(deviceInsertReq);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
}
