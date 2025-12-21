package org.pms.trigger.facade;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.dto.command.CommandRespDTO;
import org.pms.api.facade.ICommandFacade;
import org.pms.application.service.CommandRespHandler;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.types.BizCode;
import org.pms.types.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令响应RPC接口实现
 * @create 2025/12/19
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class CommandFacade implements ICommandFacade {
	
	@Resource
	private CommandRespHandler commandRespHandler;
	
	/**
	 * 批量保存指令响应
	 *
	 * @param commandList 指令响应列表
	 * @return RPC响应
	 */
	@Override
	@PostMapping("/device/command/batch-save")
	public Response<Boolean> batchHandleCommandResp(@RequestBody List<CommandRespDTO> commandList) {
		log.info("接收到批量指令响应，数量: {}", commandList != null ? commandList.size() : 0);
		
		if (Objects.isNull(commandList) || commandList.isEmpty()) {
			return Response.<Boolean>builder()
					.code(BizCode.PARAMETER_IS_NULL.getCode())
					.message(BizCode.PARAMETER_IS_NULL.getMessage())
					.data(false)
					.build();
		}
		try {
			// 遍历处理每条指令响应
			for (CommandRespDTO apiDto : commandList) {
				CommandExecutionStatusVO commandStatus = CommandExecutionStatusVO.valueOf(apiDto.getCommandState());
				commandRespHandler.handleCommandResponse(
						apiDto.getTaskId(),
						Long.parseLong(apiDto.getDeviceSN()),
						commandStatus,
						apiDto.getCommandResult(),
						apiDto.toString()
				);
			}
			
			log.info("批量指令响应保存完成");
			return Response.<Boolean>builder()
					.code(BizCode.SUCCESS.getCode())
					.message(BizCode.SUCCESS.getMessage())
					.data(true)
					.build();
			
		} catch (Exception e) {
			log.error("批量保存指令响应异常", e);
			return Response.<Boolean>builder()
					.message("批量保存设备数据失败: " + e.getMessage())
					.data(false)
					.build();
		}
	}
	
	/**
	 * 单条保存指令响应（用于重试）
	 *
	 * @param commandResponse 指令响应
	 * @return RPC响应
	 */
	@Override
	@PostMapping("/device/command/save")
	public Response<Boolean> handleCommandResp(@RequestBody CommandRespDTO commandResponse) {
		log.info("接收到单条指令响应，taskId: {}", commandResponse != null ? commandResponse.getTaskId() : null);
		
		if (Objects.isNull(commandResponse)) {
			return Response.<Boolean>builder()
					.code(BizCode.PARAMETER_IS_NULL.getCode())
					.message(BizCode.PARAMETER_IS_NULL.getMessage())
					.data(false)
					.build();
		}
		try {
			CommandExecutionStatusVO commandStatus = CommandExecutionStatusVO.valueOf(commandResponse.getCommandState());
			commandRespHandler.handleCommandResponse(
					commandResponse.getTaskId(),
					Long.parseLong(commandResponse.getDeviceSN()),
					commandStatus,
					commandResponse.getCommandResult(),
					commandResponse.toString()
			);
			
			log.info("单条指令响应保存完成，taskId: {}", commandResponse.getTaskId());
			return Response.<Boolean>builder()
					.code(BizCode.SUCCESS.getCode())
					.message(BizCode.SUCCESS.getMessage())
					.data(true)
					.build();
			
		} catch (Exception e) {
			log.error("保存单条指令响应异常，taskId: {}", commandResponse.getTaskId(), e);
			return Response.<Boolean>builder()
					.message("保存指令响应失败: " + e.getMessage())
					.data(false)
					.build();
		}
	}
	
}
