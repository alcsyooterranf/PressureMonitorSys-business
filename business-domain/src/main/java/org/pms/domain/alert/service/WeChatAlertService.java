package org.pms.domain.alert.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.pms.domain.devicedata.model.entity.DeviceDataEntity;
import org.pms.domain.terminal.model.entity.DeviceEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

/**
 * 微信公众号告警推送服务
 * <p>
 * 功能:当设备监控数据异常时,通过微信公众号发送模板消息通知用户
 * <p>
 * 使用流程:
 * 1. 用户关注公众号
 * 2. 用户绑定设备(将OpenID和设备关联)
 * 3. 设备异常时,查询设备绑定的用户OpenID
 * 4. 发送模板消息到用户微信
 * <p>
 * 模板消息格式示例:
 * ┌─────────────────────────┐
 * │ 【压力告警】            │
 * │ 设备压力超过阈值!       │
 * │                         │
 * │ 设备编号: 12345         │
 * │ 当前压力: 150 Pa        │
 * │ 压力阈值: 100 Pa        │
 * │ 告警时间: 2024-11-24... │
 * │                         │
 * │ 请及时处理!             │
 * └─────────────────────────┘
 *
 * @author zeal
 * @since 2024-11-24
 */
@Slf4j
@Service
public class WeChatAlertService {
	
	/**
	 * 微信公众号服务对象
	 * 由WeChatConfig配置类创建
	 */
	@Resource
	private WxMpService wxMpService;
	
	/**
	 * 告警模板消息ID
	 * 需要在微信公众平台申请模板消息,获取模板ID
	 * <p>
	 * 申请步骤:
	 * 1. 登录微信公众平台
	 * 2. 功能 -> 模板消息 -> 模板库
	 * 3. 选择"告警通知"类模板
	 * 4. 添加模板,获取模板ID
	 */
	@Value("${wechat.mp.alert-template-id}")
	private String alertTemplateId;
	
	/**
	 * 发送压力告警消息
	 * <p>
	 * 这个方法会被告警服务调用,当检测到压力异常时发送微信消息
	 *
	 * @param data   监控数据(包含压力、温度、电压等)
	 * @param device 设备信息(包含设备编号、阈值等)
	 */
	public void sendPressureAlert(DeviceDataEntity data, DeviceEntity device) {
		try {
			// 1. 获取用户OpenID
			// OpenID是用户在公众号中的唯一标识
			// 这里使用customerAccount作为OpenID(实际项目中需要维护OpenID映射表)
			String openId = device.getCustomerAccount();
			
			if (openId == null || openId.isEmpty()) {
				log.warn("设备未绑定用户,无法发送微信告警: deviceSN={}", device.getDeviceSN());
				return;
			}
			
			// 2. 构建模板消息
			WxMpTemplateMessage templateMessage = buildTemplateMessage(openId, data, device);
			
			// 3. 发送模板消息
			// sendTemplateMsg方法会返回消息ID(msgId)
			String msgId = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
			
			log.info("微信告警推送成功: deviceSN={}, openId={}, msgId={}",
					device.getDeviceSN(), openId, msgId);
			
		} catch (WxErrorException e) {
			// 微信API调用失败
			// 常见错误码:
			// 40001: AppSecret错误
			// 40003: OpenID错误
			// 43004: 用户未关注公众号
			// 45047: 模板消息发送次数超限
			log.error("微信告警推送失败: deviceSN={}, errorCode={}, errorMsg={}",
					device.getDeviceSN(), e.getError().getErrorCode(), e.getError().getErrorMsg(), e);
		} catch (Exception e) {
			log.error("微信告警推送异常: deviceSN={}", device.getDeviceSN(), e);
		}
	}
	
	/**
	 * 构建模板消息
	 * <p>
	 * 模板消息由多个字段组成,每个字段有key和value
	 * key对应模板中的{{keyword1.DATA}}等占位符
	 *
	 * @param openId 用户OpenID
	 * @param data   监控数据
	 * @param device 设备信息
	 * @return 模板消息对象
	 */
	private WxMpTemplateMessage buildTemplateMessage(String openId, DeviceDataEntity data, DeviceEntity device) {
		// 创建模板消息构建器
		WxMpTemplateMessage.WxMpTemplateMessageBuilder builder = WxMpTemplateMessage.builder()
				.toUser(openId)              // 接收用户的OpenID
				.templateId(alertTemplateId) // 模板ID
				.url("https://your-domain.com/alert/" + data.getId());  // 点击消息跳转的URL(可选)
		
		// 构建模板消息
		WxMpTemplateMessage templateMessage = builder.build();
		
		// 添加消息头部(红色,醒目)
		// first字段通常用于消息标题
		templateMessage.addData(new WxMpTemplateData("first",
				"【压力告警】设备压力超过阈值!",
				"#FF0000"));  // 红色
		
		// 添加设备编号
		// keyword1对应模板中的{{keyword1.DATA}}
		templateMessage.addData(new WxMpTemplateData("keyword1",
				device.getDeviceSN().toString(),
				"#173177"));  // 深蓝色
		
		// 添加当前压力值
		templateMessage.addData(new WxMpTemplateData("keyword2",
				data.getPressure() + " Pa",
				"#FF0000"));  // 红色(异常值)
		
		// 添加压力阈值
		templateMessage.addData(new WxMpTemplateData("keyword3",
				device.getPressureUpperBound() + " Pa",
				"#173177"));
		
		// 添加告警时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		templateMessage.addData(new WxMpTemplateData("keyword4",
				sdf.format(data.getCreateTime()),
				"#173177"));
		
		// 添加其他监控数据(可选)
		String remark = String.format("温度: %.2f℃, 电压: %dmV, 请及时处理!",
				data.getTemperature() / 100.0,
				data.getVoltage());
		templateMessage.addData(new WxMpTemplateData("remark",
				remark,
				"#173177"));
		
		return templateMessage;
	}
	
	/**
	 * 发送温度告警消息
	 * <p>
	 * 与压力告警类似,只是消息内容不同
	 */
	public void sendTemperatureAlert(DeviceDataEntity data, DeviceEntity device) {
		try {
			String openId = device.getCustomerAccount();
			if (openId == null || openId.isEmpty()) {
				log.warn("设备未绑定用户,无法发送微信告警: deviceSN={}", device.getDeviceSN());
				return;
			}
			
			WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
					.toUser(openId)
					.templateId(alertTemplateId)
					.url("https://your-domain.com/alert/" + data.getId())
					.build();
			
			templateMessage.addData(new WxMpTemplateData("first",
					"【温度告警】设备温度异常!",
					"#FF0000"));
			
			templateMessage.addData(new WxMpTemplateData("keyword1",
					device.getDeviceSN().toString(),
					"#173177"));
			
			templateMessage.addData(new WxMpTemplateData("keyword2",
					String.format("%.2f ℃", data.getTemperature() / 100.0),
					"#FF0000"));
			
			templateMessage.addData(new WxMpTemplateData("keyword3",
					String.format("%d - %d ℃",
							device.getTemperatureLowerBound() / 100,
							device.getTemperatureUpperBound() / 100),
					"#173177"));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			templateMessage.addData(new WxMpTemplateData("keyword4",
					sdf.format(data.getCreateTime()),
					"#173177"));
			
			templateMessage.addData(new WxMpTemplateData("remark",
					"请及时处理!",
					"#173177"));
			
			String msgId = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
			log.info("微信温度告警推送成功: deviceSN={}, msgId={}", device.getDeviceSN(), msgId);
			
		} catch (Exception e) {
			log.error("微信温度告警推送失败: deviceSN={}", device.getDeviceSN(), e);
		}
	}
	
	/**
	 * 发送电压告警消息
	 */
	public void sendVoltageAlert(DeviceDataEntity data, DeviceEntity device) {
		try {
			String openId = device.getCustomerAccount();
			if (openId == null || openId.isEmpty()) {
				log.warn("设备未绑定用户,无法发送微信告警: deviceSN={}", device.getDeviceSN());
				return;
			}
			
			WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
					.toUser(openId)
					.templateId(alertTemplateId)
					.url("https://your-domain.com/alert/" + data.getId())
					.build();
			
			templateMessage.addData(new WxMpTemplateData("first",
					"【电压告警】设备电压异常!",
					"#FF0000"));
			
			templateMessage.addData(new WxMpTemplateData("keyword1",
					device.getDeviceSN().toString(),
					"#173177"));
			
			templateMessage.addData(new WxMpTemplateData("keyword2",
					data.getVoltage() + " mV",
					"#FF0000"));
			
			templateMessage.addData(new WxMpTemplateData("keyword3",
					String.format("%d - %d mV",
							device.getVoltageLowerBound(),
							device.getVoltageUpperBound()),
					"#173177"));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			templateMessage.addData(new WxMpTemplateData("keyword4",
					sdf.format(data.getCreateTime()),
					"#173177"));
			
			templateMessage.addData(new WxMpTemplateData("remark",
					"请及时处理!",
					"#173177"));
			
			String msgId = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
			log.info("微信电压告警推送成功: deviceSN={}, msgId={}", device.getDeviceSN(), msgId);
			
		} catch (Exception e) {
			log.error("微信电压告警推送失败: deviceSN={}", device.getDeviceSN(), e);
		}
	}
	
}

