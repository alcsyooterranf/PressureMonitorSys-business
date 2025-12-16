package org.pms.infrastructure.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据模型
 * @create 2025/12/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandMetaPO {

	private Long id;
	private Long pipelineId;
	private String serviceIdentifier;
	private String name;
	private Integer version;
	private String payloadSchema;
	private Short status;
	private String remark;
	private Date createTime;
	private Date updateTime;

}
