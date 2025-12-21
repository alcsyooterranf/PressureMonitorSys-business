package org.pms.application.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.alert.port.IAlertPushPort;
import org.pms.domain.devicedata.model.entity.DeviceDataEntity;
import org.pms.domain.devicedata.service.IDeviceDataService;
import org.pms.domain.terminal.model.entity.DeviceEntity;
import org.pms.domain.terminal.service.IDeviceService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 保存设备上报的数据 - 用例级的服务
 * @create 2025/12/18
 */
@Slf4j
@Service
public class DeviceDataHandler {
	
	@Resource
	private IDeviceDataService deviceDataService;
	@Resource
	private IDeviceService deviceService;
	@Resource
	private IAlertPushPort alertPushPort;
	
	public void saveDeviceData(DeviceDataEntity deviceDataEntity) {
		// 1. 保存设备数据
		// TODO: 改为先落库, 然后发布应用事件, 确保落库成功后再推送异常数据
		deviceDataService.addDeviceData(deviceDataEntity);
		
		// 2. 主动推送异常数据
		if (deviceDataEntity.hasAbnormal()) {
			log.info("检测到设备异常, deviceSN={}, abnormalDesc={}", deviceDataEntity.getDeviceSN(), deviceDataEntity.getAbnormalDesc());
			// 查询设备信息（用于推送）
			DeviceEntity deviceEntity = deviceService.queryParameterLimitsBySN(deviceDataEntity.getDeviceSN());
			if (Objects.isNull(deviceEntity)) {
				log.warn("设备不存在，无法推送告警, deviceSN={}", deviceDataEntity.getDeviceSN());
				return;
			}
			
			// 推送异常告警
			alertPushPort.pushToWebSocket(deviceDataEntity, deviceEntity);
//			alertPushPort.pushToWeChat(deviceDataEntity, deviceEntity);
		}
	}
	
}
