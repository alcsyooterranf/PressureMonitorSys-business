package org.pms.api.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceQueryView {
	
	private Long id;
	private Long pipelineId;
	private String pipelineSN;
	private String pipelineName;
	private String deviceSN;
	private String protocolSN;
	private Integer status;
	private Long templateId;
	private String location;
	private String customerAccount;
	private Integer temperatureUpperBound;
	private Integer temperatureLowerBound;
	private Integer voltageUpperBound;
	private Integer voltageLowerBound;
	private Integer pressureUpperBound;
	private Integer pressureLowerBound;
	private Integer localAtmosphericPressure;
	private String longitude;
	private String latitude;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	private String updateBy;
	private Boolean binded;
	private String remark;
	
}
