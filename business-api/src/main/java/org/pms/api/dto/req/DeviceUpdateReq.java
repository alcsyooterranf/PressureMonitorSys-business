package org.pms.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceUpdateReq {
	
	// 主键不能为空
	private Long id;
	// 产品ID不能为空
	private Long pipelineId;
	private Long deviceSN;
	private Integer status;
	private Long templateId;
	private Integer temperatureUpperBound;
	private Integer temperatureLowerBound;
	private Integer voltageUpperBound;
	private Integer voltageLowerBound;
	private Integer pressureUpperBound;
	private Integer pressureLowerBound;
	private Integer localAtmosphericPressure;
	private String location;
	private String longitude;
	private String latitude;
	private String remark;
	
}
