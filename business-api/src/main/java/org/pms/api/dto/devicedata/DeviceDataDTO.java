package org.pms.api.dto.devicedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 设备数据传输对象（RPC通信）
 * <p>
 * 用途：网关通过RPC将设备数据发送给后端服务
 * <p>
 * 与Domain层DTO的区别：
 * - Domain层：BaseDeviceDataChangeReportDTO（接收AEP消息，包含AEP特有字段）
 * - API层：DeviceDataDTO（RPC通信，只包含业务核心字段）
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2025/11/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDataDTO implements Serializable {
	
	/**
	 * 设备ID
	 */
	private String deviceId;
	
	/**
	 * 管道ID
	 */
	private String pipelineId;
	
	/**
	 * 服务ID（类似于指令服务中的唯一标识）
	 */
	private String serviceId;
	
	/**
	 * 上报时间戳
	 */
	private Long timestamp;
	
	/**
	 * 监测参数（业务数据）
	 */
	private MonitorParameterDTO payload;
	
	/**
	 * 租户ID
	 */
	private String tenantId;
	
	/**
	 * 协议类型
	 */
	private String protocol;
	
	/**
	 * 设备类型（可选）
	 */
	private String deviceType;
	
	/**
	 * 合作伙伴ID（可选）
	 */
	private String assocAssetId;
	
	/**
	 * NB终端sim卡标识（可选）
	 */
	private String IMSI;
	
	/**
	 * NB终端设备识别号（可选）
	 */
	private String IMEI;
	
}

