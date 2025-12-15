package org.pms.api.dto.devicedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 监测参数DTO（RPC通信）
 * <p>
 * 用途：设备上报的监测数据（压力、温度、电压等）
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/11/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorParameterDTO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	/**
	 * 压力值（kPa）
	 */
	private String pressure;
	
	/**
	 * 温度值（℃）
	 */
	private String temperature;
	
	/**
	 * 电压值（V）
	 */
	private Integer voltage;
	
}

