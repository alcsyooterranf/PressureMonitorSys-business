package org.pms.trigger.facade;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.dto.devicedata.DeviceDataDTO;
import org.pms.api.facade.IDeviceDataFacade;
import org.pms.application.converter.ApiToDomainConverter;
import org.pms.application.service.DeviceDataHandler;
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
 * @create 2025-11-24
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class DeviceDataFacade implements IDeviceDataFacade {
	
	@Resource
	private DeviceDataHandler deviceDataHandler;
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
	public Response<Boolean> batchHandleDeviceData(@RequestBody List<DeviceDataDTO> dataList) {
		log.info("接收到批量设备数据，数量: {}", dataList != null ? dataList.size() : 0);
		
		if (Objects.isNull(dataList) || dataList.isEmpty()) {
			return Response.<Boolean>builder()
					.code(BizCode.PARAMETER_IS_NULL.getCode())
					.message(BizCode.PARAMETER_IS_NULL.getMessage())
					.data(false)
					.build();
		}
		
		try {
			for (DeviceDataDTO apiDto : dataList) {
				try {
					// 1. 转换API DTO为Domain Entity
					var deviceDataEntity = apiToDomainConverter.convertToDeviceDataEntity(apiDto);
					if (deviceDataEntity == null) {
						log.warn("设备数据转换失败，跳过, deviceId={}", apiDto.getDeviceId());
						continue;
					}
					
					// 2. 调用Domain层服务保存数据（包含异常检查和推送）
					deviceDataHandler.saveDeviceData(deviceDataEntity);
					
				} catch (Exception e) {
					log.error("保存设备数据失败，deviceId: {}, error: {}", apiDto.getDeviceId(), e.getMessage(), e);
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
	public Response<Boolean> handleDeviceData(@RequestBody DeviceDataDTO data) {
		log.info("接收到单条设备数据，deviceId: {}", data != null ? data.getDeviceId() : null);
		
		if (Objects.isNull(data)) {
			return Response.<Boolean>builder()
					.code(BizCode.PARAMETER_IS_NULL.getCode())
					.message(BizCode.PARAMETER_IS_NULL.getMessage())
					.data(false)
					.build();
		}
		
		try {
			var deviceDataEntity = apiToDomainConverter.convertToDeviceDataEntity(data);
			if (deviceDataEntity == null) {
				return Response.<Boolean>builder()
						.code("400")
						.message("设备数据转换失败")
						.data(false)
						.build();
			}
			
			deviceDataHandler.saveDeviceData(deviceDataEntity);
			
			log.info("单条设备数据保存成功，deviceId: {}", data.getDeviceId());
			return Response.<Boolean>builder()
					.code(BizCode.SUCCESS.getCode())
					.message(BizCode.SUCCESS.getMessage())
					.data(true)
					.build();
			
		} catch (Exception e) {
			log.error("保存单条设备数据异常，deviceId: {}", data.getDeviceId(), e);
			return Response.<Boolean>builder()
					.message("保存设备数据失败: " + e.getMessage())
					.data(false)
					.build();
		}
	}
	
}

