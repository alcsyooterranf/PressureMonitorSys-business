package org.pms.trigger.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pms.types.Constants;
import com.pms.types.ResponseCode;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.common.HttpResponse;
import org.pms.application.query.IPipelineQueryService;
import org.pms.api.dto.req.PipelineQueryCondition;
import org.pms.api.dto.resp.PipelineQueryView;
import org.pms.api.dto.req.PipelineInsertReq;
import org.pms.api.dto.req.PipelineUpdateReq;
import org.pms.domain.terminal.service.IPipelineService;
import org.pms.api.common.PageResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/pipeline_manage/")
public class PipelineManageController {
	
	private static final String SECURITY_CONTEXT_HEADER = Constants.SECURITY_CONTEXT_HEADER;
	@Resource
	private IPipelineService pipelineService;
	@Resource
	private IPipelineQueryService pipelineQueryService;
	
	@RequestMapping(value = "query_pipeline_by_conditions", method = RequestMethod.POST)
	public HttpResponse<PageResponse<PipelineQueryView>> queryPipelineByConditions(@RequestBody PipelineQueryCondition request) {
		PageResponse<PipelineQueryView> pipelineViewPage = pipelineQueryService.queryPipelineViewPage(request);
		return HttpResponse.<PageResponse<PipelineQueryView>>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.data(pipelineViewPage)
				.build();
	}
	
	@RequestMapping(value = "delete_pipeline_by_id", method = RequestMethod.POST)
	public HttpResponse<String> deletePipelineById(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
												   @RequestParam @Min(1) Long id) throws JsonProcessingException {
		pipelineService.deletePipelineById(id, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	@RequestMapping(value = "update_pipeline", method = RequestMethod.POST)
	public HttpResponse<String> updatePipeline(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
											   @RequestBody @Valid PipelineUpdateReq pipelineUpdateReq) throws JsonProcessingException {
		pipelineService.updatePipeline(pipelineUpdateReq, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	@RequestMapping(value = "add_pipeline", method = RequestMethod.POST)
	public HttpResponse<String> addPipeline(@RequestBody PipelineInsertReq pipelineInsertReq) {
		pipelineService.addPipeline(pipelineInsertReq);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
}
