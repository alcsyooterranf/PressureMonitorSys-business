package org.pms.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.command.model.entity.CommandExecutionEntity;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.domain.command.service.stateflow.IStateHandler;
import org.pms.types.Result;
import org.springframework.stereotype.Service;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令响应处理服务 - 用例级的服务
 * 职责：
 * 1. 接收网关推送的指令响应
 * 2. 更新指令执行记录
 * 3. 触发状态机流转
 * @create 2025/12/18
 */
@Slf4j
@Service
public class CommandRespHandler {
	
	@Resource
	private IStateHandler stateHandler;
	
	/**
	 * 处理指令响应
	 *
	 * @param aepTaskId     AEP任务ID
	 * @param deviceId      设备ID
	 * @param targetStatus  目标指令状态
	 * @param commandResult 指令执行结果
	 * @param rawCallback   原始回调JSON
	 */
	public void handleCommandResponse(Long aepTaskId, Long deviceId, CommandExecutionStatusVO targetStatus,
									  JsonNode commandResult, String rawCallback) {
		log.info("开始处理指令响应, aepTaskId={}, deviceId={}, targetStatus={}", aepTaskId, deviceId, targetStatus);
		CommandExecutionEntity ctx = CommandExecutionEntity.builder()
				.aepTaskId(aepTaskId)
				.deviceId(deviceId)
				.status(targetStatus)
				.resultDetail(commandResult.toString())
				.lastRawCallback(rawCallback)
				.build();
		Result result = switch (targetStatus) {
			case SAVED -> stateHandler.onSaved(ctx);
			case SENT -> stateHandler.onSent(ctx);
			case DELIVERED -> stateHandler.onDelivered(ctx);
			case COMPLETED -> stateHandler.onCompleted(ctx);
			case TTL_TIMEOUT -> stateHandler.onTtlTimeout(ctx);
			case TIMEOUT -> stateHandler.onTimeout(ctx);
			default -> Result.buildErrorResult();
		};
		if (result.isSuccess()) {
			log.info("指令响应处理成功, aepTaskId={}, deviceId={}, targetStatus={}", aepTaskId, deviceId, targetStatus);
		} else {
			log.error("指令响应处理失败, aepTaskId={}, deviceId={}, targetStatus={}", aepTaskId, deviceId, targetStatus);
		}
	}
	
}
