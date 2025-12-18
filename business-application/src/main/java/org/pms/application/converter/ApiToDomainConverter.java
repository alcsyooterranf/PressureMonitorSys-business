package org.pms.application.converter;

import lombok.extern.slf4j.Slf4j;
import org.pms.api.dto.devicedata.DeviceDataDTO;
import org.pms.api.dto.devicedata.MonitorParameterDTO;
import org.pms.domain.devicedata.model.entity.DeviceDataEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * API层DTO到Domain层Entity的转换器
 * <p>
 * 用途：将网关通过RPC发送的API层DTO转换为后端Domain层Entity
 * <p>
 * 转换关系：
 * - DeviceDataDTO (API) → DeviceDataEntity (Domain)
 * - CommandResponseDTO (API) → CommandExecutionEntity (Domain)
 *
 * @author refactor
 * @date 2025-11-24
 */
@Slf4j
@Component
public class ApiToDomainConverter {

	/**
	 * 转换设备数据：API DTO → Domain Entity
	 *
	 * @param apiDto API层设备数据DTO
	 * @return Domain层设备数据Entity
	 */
	public DeviceDataEntity convertToDeviceDataEntity(DeviceDataDTO apiDto) {
		if (apiDto == null) {
			return null;
		}

		MonitorParameterDTO payload = apiDto.getPayload();
		if (payload == null) {
			log.warn("设备数据payload为空, deviceId={}", apiDto.getDeviceId());
			return null;
		}

		return DeviceDataEntity.builder()
				.tenantId(apiDto.getTenantId())
				.deviceSN(apiDto.getDeviceId())
				.protocol(apiDto.getProtocol())
				.pipelineSN(apiDto.getPipelineId())
				// 转换监测参数
				.pressure(parseInteger(payload.getPressure()))
				.temperature(parseInteger(payload.getTemperature()))
				.voltage(payload.getVoltage())
				// 设置创建时间
				.createTime(apiDto.getTimestamp() != null ? new Date(apiDto.getTimestamp()) : new Date())
				// 初始状态
				.processState(false)
				.removed(false)
				.build();
	}

	/**
	 * 解析字符串为Integer
	 * 处理可能的数字格式异常
	 *
	 * @param value 字符串值
	 * @return Integer值，解析失败返回null
	 */
	private Integer parseInteger(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			log.warn("解析Integer失败: value={}", value, e);
			return null;
		}
	}

}

