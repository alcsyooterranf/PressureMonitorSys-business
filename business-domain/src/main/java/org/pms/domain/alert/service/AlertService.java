package org.pms.domain.alert.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.alert.model.AlertMessage;
import org.pms.domain.alert.service.impl.AlertWebSocketHandler;
import org.pms.domain.devicedata.model.entity.DeviceDataEntity;
import org.pms.domain.terminal.model.entity.DeviceEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 告警服务实现类
 * <p>
 * 这个类是告警系统的核心
 * 负责:
 * 1. 判断异常类型(压力、温度、电压)
 * 2. 确定严重程度(CRITICAL、WARNING、INFO)
 * 3. 异步发送微信推送
 * 4. 实时发送WebSocket推送
 * <p>
 * 告警流程:
 * ┌─────────────────────────────────────────────────┐
 * │ 1. 设备上报数据                                 │
 * │    ↓                                            │
 * │ 2. 计算abnormalFlag(在DataReportPO中)          │
 * │    ↓                                            │
 * │ 3. 如果abnormalFlag > 0,调用processDataAlert   │
 * │    ↓                                            │
 * │ 4. 构建AlertMessage                             │
 * │    ↓                                            │
 * │ 5. 异步发送微信推送(不阻塞主流程)              │
 * │    ↓                                            │
 * │ 6. 实时发送WebSocket推送(毫秒级)               │
 * │    ↓                                            │
 * │ 7. 前端显示红灯和弹窗                           │
 * └─────────────────────────────────────────────────┘
 *
 * @author zeal
 * @since 2024-11-24
 */
@Slf4j
@Service
public class AlertService {
	
	/**
	 * 微信告警服务
	 * 用于发送微信公众号模板消息
	 */
	@Resource
	private WeChatAlertService weChatAlertService;
	
	/**
	 * WebSocket处理器
	 * 用于实时推送告警消息到前端
	 */
	@Resource
	private AlertWebSocketHandler webSocketHandler;
	
	/**
	 * 线程池
	 * 用于异步发送微信推送,避免阻塞主流程
	 * <p>
	 * 这个线程池在ThreadPoolConfig中配置
	 */
	@Resource
	private ThreadPoolExecutor executor;
	
	/**
	 * 处理设备数据告警
	 * <p>
	 * 这个方法会在DataReportRepository保存数据后被调用
	 *
	 * @param data   监控数据
	 * @param device 设备信息
	 */
	public void processDataAlert(DeviceDataEntity data, DeviceEntity device) {
		// 1. 判断是否有异常
		// abnormalFlag是一个位标志:
		// bit0(1): 压力超上限
		// bit1(2): 压力低于下限
		// bit2(4): 温度超上限
		// bit3(8): 温度低于下限
		// bit4(16): 电压超上限
		// bit5(32): 电压低于下限
		// TODO: service服务判断
//		if (data.getAbnormalFlag() == 0) {
//			// 没有异常,直接返回
//			return;
//		}
		
		log.info("检测到设备异常: deviceSN={}, abnormalFlag={}",
				data.getDeviceSN(), data.getAbnormalFlagVO());
		
		// 2. 构建告警消息
		AlertMessage alert = buildAlertMessage(data, device);
		
		// 3. 异步发送微信推送
		// 使用线程池异步执行,避免阻塞主流程
		// 即使微信推送失败,也不影响数据保存和WebSocket推送
		if (weChatAlertService != null) {
			executor.execute(() -> {
				try {
					sendWeChatAlert(data, device);
				} catch (Exception e) {
					log.error("微信推送失败: deviceSN={}", data.getDeviceSN(), e);
					// 微信推送失败不影响其他流程
				}
			});
		}
		
		// 4. 实时发送WebSocket推送
		// 这个是同步的,但速度很快(毫秒级)
		if (webSocketHandler != null) {
			sendWebSocketAlert(alert, device);
			
			// 5. 如果是严重告警,广播给所有管理员
			if ("CRITICAL".equals(alert.getSeverity())) {
				log.warn("严重告警,广播给所有在线用户: deviceSN={}, alertType={}",
						data.getDeviceSN(), alert.getAlertType());
				webSocketHandler.broadcastAlert(alert);
			}
		}
	}
	
	/**
	 * 构建告警消息对象
	 * <p>
	 * 将DataReportPO和DevicePO的数据组装成AlertMessage
	 *
	 * @param data   监控数据
	 * @param device 设备信息
	 * @return 告警消息对象
	 */
	private AlertMessage buildAlertMessage(DeviceDataEntity data, DeviceEntity device) {
		// 确定告警类型
		String alertType = determineAlertType((short) data.getAbnormalFlagVO().ordinal());
		
		// 确定严重程度
		String severity = determineSeverity(data, device);
		
		// 解析异常描述
		String abnormalDesc = parseAbnormalFlag((short) data.getAbnormalFlagVO().ordinal());
		
		// 构建AlertMessage对象
		return AlertMessage.builder()
				.id(data.getId())
				.deviceSN(data.getDeviceSN())
				.pressure(data.getPressure())
				.pressureUpperBound(device.getPressureUpperBound())
				.pressureLowerBound(device.getPressureLowerBound())
				.temperature(data.getTemperature())
				.temperatureUpperBound(device.getTemperatureUpperBound())
				.temperatureLowerBound(device.getTemperatureLowerBound())
				.voltage(data.getVoltage())
				.voltageUpperBound(device.getVoltageUpperBound())
				.voltageLowerBound(device.getVoltageLowerBound())
				.abnormalDesc(abnormalDesc)
				.alertTime(data.getCreateTime())
				.alertType(alertType)
				.severity(severity)
				.longitude(device.getLongitude())
				.latitude(device.getLatitude())
				.build();
	}
	
	/**
	 * 确定告警类型
	 * <p>
	 * 根据abnormalFlag的位标志确定是哪种类型的异常
	 * 如果有多个异常,返回优先级最高的
	 * <p>
	 * 优先级: 压力 > 温度 > 电压
	 *
	 * @param abnormalFlag 异常标志
	 * @return 告警类型
	 */
	private String determineAlertType(short abnormalFlag) {
		// 使用位运算检查每一位
		// & 运算:两个位都为1时结果为1
		
		// 检查压力异常(优先级最高)
		if ((abnormalFlag & 1) != 0) {
			return "PRESSURE_HIGH";  // bit0=1,压力超上限
		}
		if ((abnormalFlag & (1 << 1)) != 0) {
			return "PRESSURE_LOW";   // bit1=1,压力低于下限
		}
		
		// 检查温度异常
		if ((abnormalFlag & (1 << 2)) != 0) {
			return "TEMPERATURE_HIGH";  // bit2=1,温度超上限
		}
		if ((abnormalFlag & (1 << 3)) != 0) {
			return "TEMPERATURE_LOW";   // bit3=1,温度低于下限
		}
		
		// 检查电压异常
		if ((abnormalFlag & (1 << 4)) != 0) {
			return "VOLTAGE_HIGH";  // bit4=1,电压超上限
		}
		if ((abnormalFlag & (1 << 5)) != 0) {
			return "VOLTAGE_LOW";   // bit5=1,电压低于下限
		}
		
		return "UNKNOWN";
	}
	
	/**
	 * 确定严重程度
	 * <p>
	 * 根据实际值与阈值的偏离程度确定严重程度
	 * <p>
	 * 规则:
	 * - 超过阈值50%以上: CRITICAL(严重)
	 * - 超过阈值20%以上: WARNING(警告)
	 * - 其他: INFO(信息)
	 *
	 * @param data   监控数据
	 * @param device 设备信息
	 * @return 严重程度
	 */
	private String determineSeverity(DeviceDataEntity data, DeviceEntity device) {
		// 检查压力
		if (data.getPressure() > device.getPressureUpperBound()) {
			double ratio = (double) data.getPressure() / device.getPressureUpperBound();
			if (ratio > 1.5) {
				return "CRITICAL";  // 超过50%
			} else if (ratio > 1.2) {
				return "WARNING";   // 超过20%
			}
		}
		
		// 检查温度
		if (data.getTemperature() > device.getTemperatureUpperBound()) {
			double ratio = (double) data.getTemperature() / device.getTemperatureUpperBound();
			if (ratio > 1.5) {
				return "CRITICAL";
			} else if (ratio > 1.2) {
				return "WARNING";
			}
		}
		
		// 检查电压
		if (data.getVoltage() < device.getVoltageLowerBound()) {
			double ratio = (double) data.getVoltage() / device.getVoltageLowerBound();
			if (ratio < 0.5) {
				return "CRITICAL";  // 低于50%
			} else if (ratio < 0.8) {
				return "WARNING";   // 低于20%
			}
		}
		
		return "INFO";
	}
	
	/**
	 * 解析异常标志,生成人类可读的描述
	 * <p>
	 * 例如: abnormalFlag=5(二进制101)
	 * 表示: bit0=1(压力超上限), bit2=1(温度超上限)
	 * 返回: "压力超上限, 温度超上限"
	 *
	 * @param abnormalFlag 异常标志
	 * @return 异常描述
	 */
	private String parseAbnormalFlag(short abnormalFlag) {
		List<String> abnormals = new ArrayList<>();
		
		// 检查每一位,生成描述
		if ((abnormalFlag & 1) != 0) {
			abnormals.add("压力超上限");
		}
		if ((abnormalFlag & (1 << 1)) != 0) {
			abnormals.add("压力低于下限");
		}
		if ((abnormalFlag & (1 << 2)) != 0) {
			abnormals.add("温度超上限");
		}
		if ((abnormalFlag & (1 << 3)) != 0) {
			abnormals.add("温度低于下限");
		}
		if ((abnormalFlag & (1 << 4)) != 0) {
			abnormals.add("电压超上限");
		}
		if ((abnormalFlag & (1 << 5)) != 0) {
			abnormals.add("电压低于下限");
		}
		
		// 用逗号连接所有异常描述
		return String.join(", ", abnormals);
	}
	
	/**
	 * 发送微信告警
	 * <p>
	 * 根据告警类型调用不同的微信推送方法
	 *
	 * @param data   监控数据
	 * @param device 设备信息
	 */
	private void sendWeChatAlert(DeviceDataEntity data, DeviceEntity device) {
		String alertType = determineAlertType((short) data.getAbnormalFlagVO().ordinal());
		
		// 根据告警类型选择不同的推送方法
		switch (alertType) {
			case "PRESSURE_HIGH":
			case "PRESSURE_LOW":
				weChatAlertService.sendPressureAlert(data, device);
				break;
			
			case "TEMPERATURE_HIGH":
			case "TEMPERATURE_LOW":
				weChatAlertService.sendTemperatureAlert(data, device);
				break;
			
			case "VOLTAGE_HIGH":
			case "VOLTAGE_LOW":
				weChatAlertService.sendVoltageAlert(data, device);
				break;
			
			default:
				log.warn("未知的告警类型: {}", alertType);
		}
	}
	
	/**
	 * 发送WebSocket告警
	 * <p>
	 * 推送消息到指定用户的前端页面
	 *
	 * @param alert  告警消息
	 * @param device 设备信息
	 */
	private void sendWebSocketAlert(AlertMessage alert, DeviceEntity device) {
		// 获取用户ID
		// 这里使用customerAccount作为用户ID
		// 实际项目中可能需要查询用户表获取真实的用户ID
		String userId = device.getCustomerAccount();
		
		if (userId == null || userId.isEmpty()) {
			log.warn("设备未绑定用户,无法发送WebSocket告警: deviceSN={}", device.getDeviceSN());
			return;
		}
		
		// 推送到指定用户
		webSocketHandler.sendAlertToUser(userId, alert);
	}
	
}

