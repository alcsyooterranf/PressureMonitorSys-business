package org.pms.api.facade;

import org.pms.api.dto.command.CommandRespDTO;
import org.pms.types.Response;

import java.util.List;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令服务RPC接口
 * @create 2025/12/15
 */
public interface ICommandFacade {
	
	/**
	 * 批量处理指令响应
	 * <p>
	 * 网关异步消费队列中的指令响应，批量发送给后端
	 *
	 * @param commandList 指令响应列表
	 * @return 响应结果
	 */
	Response<Boolean> batchHandleCommandResp(List<CommandRespDTO> commandList);
	
	/**
	 * 单条处理指令响应（用于重试）
	 *
	 * @param commandResponse 指令响应数据
	 * @return 响应结果
	 */
	Response<Boolean> handleCommandResp(CommandRespDTO commandResponse);
}
