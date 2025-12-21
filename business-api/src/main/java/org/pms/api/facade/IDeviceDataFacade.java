package org.pms.api.facade;

import org.pms.api.dto.devicedata.DeviceDataDTO;
import org.pms.types.Response;

import java.util.List;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令服务RPC接口
 * @create 2025/11/27
 */
public interface IDeviceDataFacade {
	
	/**
	 * 批量处理设备数据
	 * <p>
	 * 网关异步消费队列中的设备数据，批量发送给后端
	 *
	 * @param dataList 设备数据列表
	 * @return 响应结果
	 */
	Response<Boolean> batchHandleDeviceData(List<DeviceDataDTO> dataList);
	
	/**
	 * 单条处理设备数据（用于重试）
	 *
	 * @param data 设备数据
	 * @return 响应结果
	 */
	Response<Boolean> handleDeviceData(DeviceDataDTO data);
	
}

