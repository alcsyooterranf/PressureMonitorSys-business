package org.pms.api.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 设备数据查询返回类
 * @create 2025/12/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDataQueryView {
	
	private String location;
	private Long pipelineId;
	private String pipelineSN;
	private Long deviceId;
	private String deviceSN;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date errorTime;
	private String pressure;
	private String temperature;
	private Integer voltage;
	private String longitude;
	private String latitude;
	
}
