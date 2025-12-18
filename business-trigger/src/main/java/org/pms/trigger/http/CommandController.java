package org.pms.trigger.http;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.common.PageResponse;
import org.pms.api.dto.req.*;
import org.pms.api.dto.resp.CommandMetaQueryView;
import org.pms.application.converter.CommandDtoConverter;
import org.pms.application.query.ICommandMetaQueryService;
import org.pms.domain.command.model.command.CreateCommandMetaCommand;
import org.pms.domain.command.model.command.CreateCommandTaskCommand;
import org.pms.domain.command.model.command.SendCommandCommand;
import org.pms.domain.command.model.command.UpdateCommandMetaCommand;
import org.pms.domain.command.service.ICommandMetaService;
import org.pms.domain.command.service.ICommandTaskService;
import org.pms.types.BizCode;
import org.pms.types.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 指令管理控制器
 * <p>
 * 包含指令元数据、指令执行记录、指令下发任务
 * <p>
 * 重构说明：
 * - 在Trigger层注入DTO转换器
 * - 将API DTO转换为Domain Command对象
 * - 调用Domain层Service处理业务逻辑
 *
 * @author alcsyooterranf
 * @author refactor
 * @date 2025/12/15
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
	@Resource
	private CommandDtoConverter commandDtoConverter;
	
	/**
	 * 创建指令元数据
	 *
	 * @param request 创建请求
	 * @return 响应
	 */
	@RequestMapping(value = "meta/create", method = RequestMethod.POST)
	public Response<String> createCommandMeta(@RequestBody @Valid CommandMetaInsertReq request) {
		// 1. API DTO → Domain Command（在Trigger层转换）
		CreateCommandMetaCommand command = commandDtoConverter.toCreateCommand(request);

		// 2. 调用Domain层服务
		commandMetaService.createCommandMeta(command);

		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.build();
	}
	
	/**
	 * 查询指令元数据（分页）
	 *
	 * @param request 查询条件
	 * @return 分页响应
	 */
	@RequestMapping(value = "meta/query", method = RequestMethod.POST)
	public Response<PageResponse<CommandMetaQueryView>> queryCommandMeta(@RequestBody CommandMetaQueryCondition request) {
		PageResponse<CommandMetaQueryView> pageResponse = commandMetaQueryService.queryCommandMetaPage(request);
		return Response.<PageResponse<CommandMetaQueryView>>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
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
	public Response<String> updateCommandMeta(@RequestBody @Valid CommandMetaUpdateReq request) {
		// 1. API DTO → Domain Command
		UpdateCommandMetaCommand command = commandDtoConverter.toUpdateCommand(request);

		// 2. 调用Domain层服务
		commandMetaService.updateCommandMeta(command);

		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
				.build();
	}
	
	/**
	 * 废弃指令元数据（将status改为3-DEPRECATED）
	 *
	 * @param id 主键ID
	 * @return 响应
	 */
	@RequestMapping(value = "meta/deprecate", method = RequestMethod.POST)
	public Response<String> deprecateCommandMeta(@RequestParam @Min(1) Long id) {
		commandMetaService.deprecateCommandMeta(id);
		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
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
	public Response<String> verifyCommandMeta(@RequestParam @Min(1) Long id) {
		commandMetaService.verifyCommandMeta(id);
		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
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
	public Response<Long> createCommandTask(@RequestBody @Valid CommandTaskCreateReq request) {
		// 1. API DTO → Domain Command
		CreateCommandTaskCommand command = commandDtoConverter.toCreateTaskCommand(request);

		// 2. 调用Domain层服务
		Long taskId = commandTaskService.createCommandTask(command);

		return Response.<Long>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
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
	// TODO: 有问题, 没有回写last_raw_callback
	@RequestMapping(value = "task/send", method = RequestMethod.POST)
	public Response<Long> sendCommand(@RequestBody @Valid CommandTaskSendReq request) {
		// 1. API DTO → Domain Command
		SendCommandCommand command = commandDtoConverter.toSendCommand(request);

		// 2. 调用Domain层服务
		Long aepTaskId = commandTaskService.sendCommand(command);

		return Response.<Long>builder()
				.code(BizCode.SUCCESS.getCode())
				.message(BizCode.SUCCESS.getMessage())
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
	public Response<String> sendCommandAsync(@RequestBody @Valid CommandTaskSendReq request) {
		// 1. API DTO → Domain Command
		SendCommandCommand command = commandDtoConverter.toSendCommand(request);

		// 2. 异步执行指令下发
		commandTaskService.sendCommandAsync(command)
				.thenAccept(aepTaskId -> {
					log.info("异步指令下发完成, taskId={}, aepTaskId={}", request.getTaskId(), aepTaskId);
				})
				.exceptionally(throwable -> {
					log.error("异步指令下发失败, taskId={}", request.getTaskId(), throwable);
					return null;
				});

		// 立即返回
		return Response.<String>builder()
				.code(BizCode.SUCCESS.getCode())
				.message("指令已提交，正在后台异步执行")
				.data("任务ID: " + request.getTaskId())
				.build();
	}
	
}
