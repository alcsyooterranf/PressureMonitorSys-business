package org.pms.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据创建请求
 * @create 2025/12/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandMetaInsertReq {

	/**
	 * 管道ID
	 */
	private Long pipelineId;

	/**
	 * 服务标识符
	 */
	private String serviceIdentifier;

	/**
	 * 指令名称
	 */
	private String name;

	/**
	 * payload schema (JSON字符串)
	 */
	private String payloadSchema;

	/**
	 * 备注
	 */
	private String remark;

}

