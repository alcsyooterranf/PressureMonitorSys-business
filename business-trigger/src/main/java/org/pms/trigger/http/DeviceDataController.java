package org.pms.trigger.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pms.types.Constants;
import com.pms.types.ResponseCode;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.common.HttpResponse;
import org.pms.application.query.IDeviceDataQueryService;
import org.pms.api.dto.req.DeviceDataQueryCondition;
import org.pms.api.dto.resp.DeviceDataQueryView;
import org.pms.domain.devicedata.service.IDeviceDataService;
import org.pms.domain.terminal.service.IDeviceService;
import org.pms.domain.terminal.service.IPipelineService;
import org.pms.api.common.PageResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/device_data/")
public class DeviceDataController {
	
	private static final String SECURITY_CONTEXT_HEADER = Constants.SECURITY_CONTEXT_HEADER;
	@Resource
	private IDeviceDataService deviceDataService;
	@Resource
	private IDeviceDataQueryService deviceDataQueryService;
	@Resource
	private IDeviceService deviceService;
	@Resource
	private IPipelineService pipelineService;
	
	@RequestMapping(value = "query_device_data_by_conditions", method = RequestMethod.POST)
	public HttpResponse<PageResponse<DeviceDataQueryView>> queryDeviceDataByConditions(@RequestBody DeviceDataQueryCondition request) {
		PageResponse<DeviceDataQueryView> deviceDataViewPage = deviceDataQueryService.queryDeviceDataPage(request);
		
		return HttpResponse.<PageResponse<DeviceDataQueryView>>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.data(deviceDataViewPage)
				.build();
	}
	
	@RequestMapping(value = "alter_device_data_by_id", method = RequestMethod.POST)
	public HttpResponse<String> alterDeviceDataById(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
													@RequestParam @Min(1) Long id) throws JsonProcessingException {
		deviceDataService.updateStatusById(id, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	@RequestMapping(value = "delete_device_data_by_id", method = RequestMethod.POST)
	public HttpResponse<String> deleteDeviceDataById(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
													 @RequestParam @Min(1) Long id) throws JsonProcessingException {
		deviceDataService.deleteDeviceDataById(id, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
}
