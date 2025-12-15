package org.pms.application.converter;

import org.pms.api.dto.devicedata.MonitorParameterDTO;
import org.springframework.stereotype.Component;

/**
 * API层DTO到Domain层DTO的转换器
 * <p>
 * 用途：将网关通过RPC发送的API层DTO转换为后端Domain层DTO
 * <p>
 * 转换关系：
 * - DeviceDataDTO (API) → BaseDeviceDataChangeReportDTO (Domain)
 * - CommandResponseDTO (API) → BaseDeviceCommandResponseDTO (Domain)
 *
 * @author refactor
 * @date 2025-11-24
 */
@Component
public class ApiToDomainConverter {
	
	/**
	 * 转换监测参数：API DTO → Domain DTO
	 *
	 * @param apiPayload API层监测参数DTO
	 * @return Domain层监测参数DTO
	 */
	private MonitorParameterDTO convertMonitorParameter(MonitorParameterDTO apiPayload) {
		if (apiPayload == null) {
			return null;
		}
		
		return MonitorParameterDTO.builder()
				.pressure(apiPayload.getPressure())
				.temperature(apiPayload.getTemperature())
				.voltage(apiPayload.getVoltage())
				.build();
	}
	
}

