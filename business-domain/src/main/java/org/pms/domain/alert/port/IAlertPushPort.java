package org.pms.domain.alert.port;

import org.pms.domain.devicedata.model.entity.DeviceDataEntity;
import org.pms.domain.terminal.model.entity.DeviceEntity;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 异常告警推送接口
 * @create 2025/12/18
 */
public interface IAlertPushPort {
	
	void pushToWebSocket(DeviceDataEntity deviceData, DeviceEntity device);
	
	void pushToWeChat(DeviceDataEntity deviceData, DeviceEntity device);
	
}
