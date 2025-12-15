package org.pms.domain.terminal.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pms.domain.terminal.model.entity.DeviceEntity;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 设备聚合根
 * @create 2025/12/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {
	
	private String pipelineName;
	private String location;
	private DeviceEntity deviceEntity;
	
}
