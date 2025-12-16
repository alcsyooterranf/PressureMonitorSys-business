package org.pms.domain.command.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pms.domain.command.model.valobj.StatusVO;

import java.util.Date;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据
 * @create 2025/12/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandMetaEntity {

	private Long id;
	private Long pipelineId;
	private String serviceIdentifier;
	private String name;
	private Integer version;
	private String payloadSchema;
	private StatusVO status;
	private String remark;
	private Date createTime;
	private Date updateTime;

}
