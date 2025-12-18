package org.pms.trigger.facade;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.dto.command.CommandResponseDTO;
import org.pms.api.dto.devicedata.DeviceDataDTO;
import org.pms.api.facade.IDeviceDataFacade;
import org.pms.application.converter.ApiToDomainConverter;
import org.pms.application.service.SaveDeviceDataService;
import org.pms.types.BizCode;
import org.pms.types.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 设备数据RPC接口实现*
 * 用途：接收网关通过RPC发送的设备数据和指令响应
 * 实现思路：
 * 1. 接收API层DTO（DeviceDataDTO、CommandResponseDTO）
 * 2. 通过ApiToDomainConverter转换为Domain层DTO
 * 3. 调用Domain层服务进行业务处理
 * 4. 返回RpcResponse统一响应
 * 端点映射：
 * - POST /api/device/data/batch-save - 批量保存设备数据
 * - POST /api/device/command/batch-save - 批量保存指令响应
 * - POST /api/device/data/save - 单条保存设备数据
 * - POST /api/device/command/save - 单条保存指令响应
 *
 * @author refactor
 * @date 2025-11-24
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class DeviceDataFacade implements IDeviceDataFacade {
	
	@Resource
	private SaveDeviceDataService saveDeviceDataService;
	@Resource
	private ApiToDomainConverter apiToDomainConverter;
	
	/**
	 * 批量保存设备数据
	 *
	 * @param dataList 设备数据列表
	 * @return RPC响应
	 */
	@Override
	@PostMapping("/device/data/batch-save")
	public Response<Boolean> batchSaveDeviceData(@RequestBody List<DeviceDataDTO> dataList) {
		try {
			log.info("接收到批量设备数据，数量: {}", dataList != null ? dataList.size() : 0);
			
			if (dataList == null || dataList.isEmpty()) {
				return Response.<Boolean>builder()
						.code(BizCode.PARAMETER_IS_NULL.getCode())
						.message(BizCode.PARAMETER_IS_NULL.getMessage())
						.data(false)
						.build();
			}
			
			// 遍历处理每条数据
			for (DeviceDataDTO apiDto : dataList) {
				try {
					// 1. 转换API DTO为Domain Entity
					var deviceDataEntity = apiToDomainConverter.convertToDeviceDataEntity(apiDto);
					if (deviceDataEntity == null) {
						log.warn("设备数据转换失败，跳过, deviceId={}", apiDto.getDeviceId());
						continue;
					}
					
					// 2. 调用Domain层服务保存数据（包含异常检查和推送）
					saveDeviceDataService.saveDeviceData(deviceDataEntity);
					
				} catch (Exception e) {
					log.error("保存设备数据失败，deviceId: {}, error: {}", apiDto.getDeviceId(), e.getMessage(), e);
					// 单条失败不影响其他数据，继续处理
				}
			}
			
			log.info("批量设备数据保存完成");
			return Response.<Boolean>builder()
					.code(BizCode.SUCCESS.getCode())
					.message(BizCode.SUCCESS.getMessage())
					.data(true)
					.build();
			
		} catch (Exception e) {
			log.error("批量保存设备数据异常", e);
			return Response.<Boolean>builder()
					.code("500")
					.message("批量保存设备数据失败: " + e.getMessage())
					.data(false)
					.build();
		}
	}
	
	/**
	 * 批量保存指令响应
	 *
	 * @param commandList 指令响应列表
	 * @return RPC响应
	 */
	@Override
	@PostMapping("/device/command/batch-save")
	public Response<Boolean> batchSaveCommandResponse(@RequestBody List<CommandResponseDTO> commandList) {
		try {
			log.info("接收到批量指令响应，数量: {}", commandList != null ? commandList.size() : 0);
			
			if (commandList == null || commandList.isEmpty()) {
				// TODO: 定义异常处理枚举
				return Response.<Boolean>builder()
						.message("指令响应列表为空")
						.data(false)
						.build();
			}
			
			// 遍历处理每条指令响应
			for (CommandResponseDTO apiDto : commandList) {
				try {
					// TODO: 调用Domain层服务保存指令响应
					// 当前Domain层没有对应的服务方法，需要后续实现
					log.warn("指令响应保存功能待实现，taskId: {}", apiDto.getTaskId());
					
				} catch (Exception e) {
					log.error("保存指令响应失败，taskId: {}, error: {}", apiDto.getTaskId(), e.getMessage(), e);
					// 单条失败不影响其他数据，继续处理
				}
			}
			
			log.info("批量指令响应保存完成");
			// TODO: 定义异常处理枚举
			return Response.<Boolean>builder()
					.message("成功")
					.data(true)
					.build();
			
		} catch (Exception e) {
			log.error("批量保存指令响应异常", e);
			// TODO: 定义异常处理枚举
			return Response.<Boolean>builder()
					.message("批量保存设备数据失败: " + e.getMessage())
					.data(false)
					.build();
		}
	}
	
	/**
	 * 单条保存设备数据（用于重试）
	 *
	 * @param data 设备数据
	 * @return RPC响应
	 */
	@Override
	@PostMapping("/device/data/save")
	public Response<Boolean> saveDeviceData(@RequestBody DeviceDataDTO data) {
		try {
			log.info("接收到单条设备数据，deviceId: {}", data != null ? data.getDeviceId() : null);
			
			if (data == null) {
				return Response.<Boolean>builder()
						.code("400")
						.message("设备数据为空")
						.data(false)
						.build();
			}
			
			// 1. 转换API DTO为Domain Entity
			var deviceDataEntity = apiToDomainConverter.convertToDeviceDataEntity(data);
			if (deviceDataEntity == null) {
				return Response.<Boolean>builder()
						.code("400")
						.message("设备数据转换失败")
						.data(false)
						.build();
			}
			
			// 2. 调用Domain层服务保存数据（包含异常检查和推送）
			saveDeviceDataService.saveDeviceData(deviceDataEntity);
			
			log.info("单条设备数据保存成功，deviceId: {}", data.getDeviceId());
			return Response.<Boolean>builder()
					.code("200")
					.message("成功")
					.data(true)
					.build();
			
		} catch (Exception e) {
			log.error("保存单条设备数据异常，deviceId: {}",
					data != null ? data.getDeviceId() : null, e);
			return Response.<Boolean>builder()
					.code("500")
					.message("保存设备数据失败: " + e.getMessage())
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
	public Response<Boolean> saveCommandResponse(@RequestBody CommandResponseDTO commandResponse) {
		try {
			log.info("接收到单条指令响应，taskId: {}",
					commandResponse != null ? commandResponse.getTaskId() : null);
			
			if (commandResponse == null) {
				// TODO: 定义异常处理枚举
				return Response.<Boolean>builder()
						.message("指令响应为空")
						.data(false)
						.build();
			}
			
			// TODO: 调用Domain层服务保存指令响应
			// 当前Domain层没有对应的服务方法，需要后续实现
			log.warn("指令响应保存功能待实现，taskId: {}", commandResponse.getTaskId());
			
			log.info("单条指令响应保存完成，taskId: {}", commandResponse.getTaskId());
			// TODO: 定义异常处理枚举
			return Response.<Boolean>builder()
					.message("成功")
					.data(true)
					.build();
			
		} catch (Exception e) {
			log.error("保存单条指令响应异常，taskId: {}",
					commandResponse != null ? commandResponse.getTaskId() : null, e);
			// TODO: 定义异常处理枚举
			return Response.<Boolean>builder()
					.message("保存指令响应失败: " + e.getMessage())
					.data(false)
					.build();
		}
	}
	
}

