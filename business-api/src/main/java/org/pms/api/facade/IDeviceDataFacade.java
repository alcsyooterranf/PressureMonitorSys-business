package org.pms.api.facade;


import org.pms.api.common.RpcResponse;
import org.pms.api.dto.command.CommandResponseDTO;
import org.pms.api.dto.devicedata.DeviceDataDTO;

import java.util.List;

/**
 * 设备数据RPC接口
 *
 * 用途：网关调用后端服务的接口定义
 *
 * 设计思路：
 * 1. 网关接收AEP消息（Domain层DTO）
 * 2. 转换为API层DTO（DeviceDataDTO、CommandResponseDTO）
 * 3. 通过Feign调用后端RPC接口
 * 4. 后端接收API层DTO，进行业务处理
 *
 * 职责：
 * 1. 批量保存设备数据
 * 2. 批量保存指令响应
 * 3. 单条保存（用于重试）
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/11/27
 */
public interface IDeviceDataFacade {

    /**
     * 批量保存设备数据
     *
     * 网关异步消费队列中的设备数据，批量发送给后端
     *
     * @param dataList 设备数据列表
     * @return 响应结果
     */
    RpcResponse<Boolean> batchSaveDeviceData(List<DeviceDataDTO> dataList);

    /**
     * 批量保存指令响应
     *
     * 网关异步消费队列中的指令响应，批量发送给后端
     *
     * @param commandList 指令响应列表
     * @return 响应结果
     */
    RpcResponse<Boolean> batchSaveCommandResponse(List<CommandResponseDTO> commandList);

    /**
     * 单条保存设备数据（用于重试）
     *
     * @param data 设备数据
     * @return 响应结果
     */
    RpcResponse<Boolean> saveDeviceData(DeviceDataDTO data);

    /**
     * 单条保存指令响应（用于重试）
     *
     * @param commandResponse 指令响应数据
     * @return 响应结果
     */
    RpcResponse<Boolean> saveCommandResponse(CommandResponseDTO commandResponse);
}

