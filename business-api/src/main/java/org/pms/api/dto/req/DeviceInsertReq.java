package org.pms.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInsertReq {
	
	private Long pipelineId;
	// AEP平台的productId
	private Long pipelineSN;
	private Long deviceSN;
	// TODO: 不知道协议编号是什么意思, 使用默认值处理
	private String protocolSN;
	// TODO: 工作模板还没想好, 暂不处理
	private Long templateId;
	// TODO: 后期可考虑删除, 与管道中的用户账号字段重复
	private String customerAccount;
	// 设备温度上限不能为空
	private Integer temperatureUpperBound;
	// 设备温度下限不能为空
	private Integer temperatureLowerBound;
	// 设备电压上限不能为空
	private Integer voltageUpperBound;
	// 设备电压下限不能为空
	private Integer voltageLowerBound;
	// 设备压力上限不能为空
	private Integer pressureUpperBound;
	// 设备压力下限不能为空
	private Integer pressureLowerBound;
	// 设备所处环境大气压力不能为空
	private Integer localAtmosphericPressure;
	// 设备位置经度不能为空
	private String longitude;
	// 设备位置纬度不能为空
	private String latitude;
	private String remark;
	
}
