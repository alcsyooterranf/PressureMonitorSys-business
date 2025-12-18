package org.pms.infrastructure.adapter.port;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.alert.port.IAlertPushPort;
import org.pms.domain.alert.service.WeChatAlertService;
import org.pms.domain.devicedata.model.entity.DeviceDataEntity;
import org.pms.domain.devicedata.model.vo.AbnormalFlagVO;
import org.pms.domain.terminal.model.entity.DeviceEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 告警推送服务实现（Infrastructure层）
 * <p>
 * 实现说明：
 * - 依赖外部服务（WebSocket RPC、微信SDK）
 * - 推送失败不抛出异常，只记录日志
 * - 支持多个异常同时推送
 * <p>
 *
 * @author refactor
 * @date 2025-12-17
 */
@Slf4j
@Service
public class AlertPushPort implements IAlertPushPort {
	
	@Resource
	private IAlterPushRpcClient alterPushRpcClient;
	@Resource
	private WeChatAlertService weChatAlertService;
	
	@Override
	public void pushToWebSocket(DeviceDataEntity deviceData, DeviceEntity device) {
		try {
			// 构建告警数据
			Map<String, Object> alertData = new HashMap<>();
			alertData.put("deviceSN", deviceData.getDeviceSN());
			alertData.put("deviceId", deviceData.getDeviceId());
			alertData.put("pipelineSN", deviceData.getPipelineSN());
			alertData.put("pipelineId", deviceData.getPipelineId());
			alertData.put("abnormalDesc", deviceData.getAbnormalDesc());
			alertData.put("abnormalCodes", AbnormalFlagVO.toCodes(deviceData.getAbnormalFlags()));
			alertData.put("pressure", deviceData.getPressure());
			alertData.put("temperature", deviceData.getTemperature());
			alertData.put("voltage", deviceData.getVoltage());
			alertData.put("createTime", deviceData.getCreateTime());
			alertData.put("customerAccount", device.getCustomerAccount());
			
			// 广播告警消息给所有在线用户
			alterPushRpcClient.broadcast(alertData);
			log.info("WebSocket告警推送成功, deviceSN={}, abnormalDesc={}",
					deviceData.getDeviceSN(), deviceData.getAbnormalDesc());
			
		} catch (Exception e) {
			log.error("WebSocket告警推送失败, deviceSN={}, abnormalDesc={}",
					deviceData.getDeviceSN(), deviceData.getAbnormalDesc(), e);
		}
	}
	
	@Override
	public void pushToWeChat(DeviceDataEntity deviceData, DeviceEntity device) {
		try {
			// 遍历所有异常类型，分别推送
			for (AbnormalFlagVO abnormalFlag : deviceData.getAbnormalFlags()) {
				if (!abnormalFlag.isAbnormal()) {
					continue;
				}
				
				switch (abnormalFlag) {
					case OVER_PRESSURE:
					case UNDER_PRESSURE:
						weChatAlertService.sendPressureAlert(deviceData, device);
						break;
					case OVER_TEMPERATURE:
					case UNDER_TEMPERATURE:
						weChatAlertService.sendTemperatureAlert(deviceData, device);
						break;
					case OVER_VOLTAGE:
					case UNDER_VOLTAGE:
						weChatAlertService.sendVoltageAlert(deviceData, device);
						break;
					default:
						log.warn("未知的异常类型, abnormalFlag={}", abnormalFlag);
				}
			}
			
		} catch (Exception e) {
			log.error("微信告警推送失败, deviceSN={}, abnormalDesc={}",
					deviceData.getDeviceSN(), deviceData.getAbnormalDesc(), e);
		}
	}
	
}

