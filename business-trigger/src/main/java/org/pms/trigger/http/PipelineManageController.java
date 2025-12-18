package org.pms.trigger.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.pms.application.converter.TerminalDtoConverter;
import org.pms.application.query.IPipelineQueryService;
import org.pms.api.dto.req.PipelineQueryCondition;
import org.pms.api.dto.resp.PipelineQueryView;
import org.pms.api.dto.req.PipelineInsertReq;
import org.pms.api.dto.req.PipelineUpdateReq;
import org.pms.domain.terminal.model.command.CreatePipelineCommand;
import org.pms.domain.terminal.model.command.UpdatePipelineCommand;
import org.pms.domain.terminal.service.IPipelineService;
import org.pms.api.common.PageResponse;
import org.pms.types.BizCode;
import org.pms.types.BizConstants;
import org.pms.types.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管道管理控制?
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
@RequestMapping("/pipeline_manage/")
public class PipelineManageController {

	private static final String SECURITY_CONTEXT_HEADER = BizConstants.SECURITY_CONTEXT_HEADER;
	@Resource
	private IPipelineService pipelineService;
	@Resource
	private IPipelineQueryService pipelineQueryService;
	@Resource
	private TerminalDtoConverter terminalDtoConverter;
	
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public Response<PageResponse<PipelineQueryView>> queryPipelineByConditions(@RequestBody PipelineQueryCondition request) {
		PageResponse<PipelineQueryView> pipelineViewPage = pipelineQueryService.queryPipelineViewPage(request);
		return Response.<PageResponse<PipelineQueryView>>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.data(pipelineViewPage)
				.build();
	}
	
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public Response<String> deletePipelineById(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
											   @RequestParam @Min(1) Long id) throws JsonProcessingException {
		pipelineService.deletePipelineById(id, securityContextEncoded);
		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.build();
	}
	
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public Response<String> updatePipeline(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
										   @RequestBody @Valid PipelineUpdateReq pipelineUpdateReq) throws JsonProcessingException {
		// 1. API DTO -> Domain Command
		UpdatePipelineCommand command = terminalDtoConverter.toUpdateCommand(pipelineUpdateReq);

		// 2. 调用Domain层服务
		pipelineService.updatePipeline(command, securityContextEncoded);

		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.build();
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public Response<String> createPipeline(@RequestBody PipelineInsertReq pipelineInsertReq) {
		// 1. API DTO -> Domain Command
		CreatePipelineCommand command = terminalDtoConverter.toCreateCommand(pipelineInsertReq);

		// 2. 调用Domain层服务
		pipelineService.addPipeline(command);

		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.build();
	}
	
}
