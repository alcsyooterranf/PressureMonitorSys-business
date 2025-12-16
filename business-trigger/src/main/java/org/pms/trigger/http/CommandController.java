package org.pms.trigger.http;

import com.pms.types.ResponseCode;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.common.HttpResponse;
import org.pms.api.common.PageResponse;
import org.pms.api.dto.req.*;
import org.pms.api.dto.resp.CommandMetaQueryView;
import org.pms.application.query.ICommandMetaQueryService;
import org.pms.domain.command.service.ICommandMetaService;
import org.pms.domain.command.service.ICommandTaskService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令管理控制器, 包含指令元数据、指令执行记录、指令下发任务
 * @create 2025/12/15
 */
@Slf4j
@Validated
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/command/")
public class CommandController {
	
	@Resource
	private ICommandMetaService commandMetaService;
	@Resource
	private ICommandMetaQueryService commandMetaQueryService;
	@Resource
	private ICommandTaskService commandTaskService;
	
	/**
	 * 创建指令元数据
	 *
	 * @param request 创建请求
	 * @return 响应
	 */
	@RequestMapping(value = "meta/create", method = RequestMethod.POST)
	public HttpResponse<String> createCommandMeta(@RequestBody @Valid CommandMetaInsertReq request) {
		commandMetaService.createCommandMeta(request);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	/**
	 * 查询指令元数据（分页）
	 *
	 * @param request 查询条件
	 * @return 分页响应
	 */
	@RequestMapping(value = "meta/query", method = RequestMethod.POST)
	public HttpResponse<PageResponse<CommandMetaQueryView>> queryCommandMeta(@RequestBody CommandMetaQueryCondition request) {
		PageResponse<CommandMetaQueryView> pageResponse = commandMetaQueryService.queryCommandMetaPage(request);
		return HttpResponse.<PageResponse<CommandMetaQueryView>>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.data(pageResponse)
				.build();
	}
	
	/**
	 * 更新指令元数据
	 *
	 * @param request 更新请求
	 * @return 响应
	 */
	@RequestMapping(value = "meta/update", method = RequestMethod.POST)
	public HttpResponse<String> updateCommandMeta(@RequestBody @Valid CommandMetaUpdateReq request) {
		commandMetaService.updateCommandMeta(request);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	/**
	 * 废弃指令元数据（将status改为3-DEPRECATED）
	 *
	 * @param id 主键ID
	 * @return 响应
	 */
	@RequestMapping(value = "meta/deprecate", method = RequestMethod.POST)
	public HttpResponse<String> deprecateCommandMeta(@RequestParam @Min(1) Long id) {
		commandMetaService.deprecateCommandMeta(id);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	/**
	 * 验证指令元数据（将status从1-UNVERIFIED变为2-VERIFIED）
	 * POST /command/verify
	 *
	 * @param id 主键ID
	 * @return 响应
	 */
	@RequestMapping(value = "meta/verify", method = RequestMethod.POST)
	public HttpResponse<String> verifyCommandMeta(@RequestParam @Min(1) Long id) {
		commandMetaService.verifyCommandMeta(id);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}

	/**
	 * 创建指令任务
	 * 只定义指令内容，不执行下发
	 *
	 * @param request 创建请求
	 * @return 响应（包含任务ID）
	 */
	@RequestMapping(value = "task/create", method = RequestMethod.POST)
	public HttpResponse<Long> createCommandTask(@RequestBody @Valid CommandTaskCreateReq request) {
		Long taskId = commandTaskService.createCommandTask(request);
		return HttpResponse.<Long>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.data(taskId)
				.build();
	}

	/**
	 * 指令下发（同步）
	 * 执行指令下发并维护状态机
	 *
	 * @param request 指令下发请求
	 * @return 响应（包含AEP任务ID）
	 */
	@RequestMapping(value = "task/send", method = RequestMethod.POST)
	public HttpResponse<Long> sendCommand(@RequestBody @Valid CommandTaskSendReq request) {
		Long aepTaskId = commandTaskService.sendCommand(request);
		return HttpResponse.<Long>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.data(aepTaskId)
				.build();
	}

	/**
	 * 指令下发（异步）
	 * 异步执行指令下发并维护状态机
	 * 立即返回，指令在后台异步执行
	 *
	 * @param request 指令下发请求
	 * @return 响应（立即返回，不包含AEP任务ID）
	 */
	@RequestMapping(value = "task/send/async", method = RequestMethod.POST)
	public HttpResponse<String> sendCommandAsync(@RequestBody @Valid CommandTaskSendReq request) {
		// 异步执行指令下发
		commandTaskService.sendCommandAsync(request)
				.thenAccept(aepTaskId -> {
					log.info("异步指令下发完成, taskId={}, aepTaskId={}", request.getTaskId(), aepTaskId);
				})
				.exceptionally(throwable -> {
					log.error("异步指令下发失败, taskId={}", request.getTaskId(), throwable);
					return null;
				});

		// 立即返回
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message("指令已提交，正在后台异步执行")
				.data("任务ID: " + request.getTaskId())
				.build();
	}

}
