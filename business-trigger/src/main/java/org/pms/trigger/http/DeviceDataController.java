package org.pms.trigger.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.pms.application.query.IDeviceDataQueryService;
import org.pms.api.dto.req.DeviceDataQueryCondition;
import org.pms.api.dto.resp.DeviceDataQueryView;
import org.pms.domain.devicedata.service.IDeviceDataService;
import org.pms.domain.terminal.service.IDeviceService;
import org.pms.domain.terminal.service.IPipelineService;
import org.pms.api.common.PageResponse;
import org.pms.types.BizCode;
import org.pms.types.BizConstants;
import org.pms.types.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/device_data/")
public class DeviceDataController {
	
	private static final String SECURITY_CONTEXT_HEADER = BizConstants.SECURITY_CONTEXT_HEADER;
	@Resource
	private IDeviceDataService deviceDataService;
	@Resource
	private IDeviceDataQueryService deviceDataQueryService;
	@Resource
	private IDeviceService deviceService;
	@Resource
	private IPipelineService pipelineService;
	
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public Response<PageResponse<DeviceDataQueryView>> queryDeviceDataByConditions(@RequestBody DeviceDataQueryCondition request) {
		PageResponse<DeviceDataQueryView> deviceDataViewPage = deviceDataQueryService.queryDeviceDataPage(request);
		
		return Response.<PageResponse<DeviceDataQueryView>>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.data(deviceDataViewPage)
				.build();
	}
	
	@RequestMapping(value = "alter", method = RequestMethod.POST)
	public Response<String> alterDeviceDataById(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
												@RequestParam @Min(1) Long id) throws JsonProcessingException {
		deviceDataService.updateStatusById(id, securityContextEncoded);
		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.build();
	}
	
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public Response<String> deleteDeviceDataById(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
												 @RequestParam @Min(1) Long id) throws JsonProcessingException {
		deviceDataService.deleteDeviceDataById(id, securityContextEncoded);
		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.build();
	}
	
}
