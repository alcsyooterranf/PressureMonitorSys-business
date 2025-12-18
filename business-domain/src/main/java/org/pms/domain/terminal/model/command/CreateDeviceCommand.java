package org.pms.domain.terminal.model.command;

import lombok.Builder;
import lombok.Value;

/**
 * 创建设备命令对象
 * <p>
 * 用途：封装创建设备的参数，替代API层的DeviceInsertReq
 *
 * @author refactor
 * @date 2025-12-18
 */
@Value
@Builder
public class CreateDeviceCommand {
	
	/**
	 * 管道ID
	 */
	Long pipelineId;
	
	/**
	 * AEP平台的productId
	 */
	Long pipelineSN;
	
	/**
	 * 设备SN
	 */
	Long deviceSN;
	
	/**
	 * 协议编号
	 */
	String protocolSN;
	
	/**
	 * 工作模板ID
	 */
	Long templateId;
	
	/**
	 * 客户账号
	 */
	String customerAccount;
	
	/**
	 * 设备温度上限
	 */
	Integer temperatureUpperBound;
	
	/**
	 * 设备温度下限
	 */
	Integer temperatureLowerBound;
	
	/**
	 * 设备电压上限
	 */
	Integer voltageUpperBound;
	
	/**
	 * 设备电压下限
	 */
	Integer voltageLowerBound;
	
	/**
	 * 设备压力上限
	 */
	Integer pressureUpperBound;
	
	/**
	 * 设备压力下限
	 */
	Integer pressureLowerBound;
	
	/**
	 * 设备所处环境大气压力
	 */
	Integer localAtmosphericPressure;
	
	/**
	 * 设备位置经度
	 */
	String longitude;
	
	/**
	 * 设备位置纬度
	 */
	String latitude;
	
	/**
	 * 备注
	 */
	String remark;
	
}

