package org.pms.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.pms.api.common.PageRequest;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据查询条件
 * @create 2025/12/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommandMetaQueryCondition extends PageRequest {
	
	/**
	 * 主键ID
	 */
	private Long id;
	
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
	 * 状态 (1-未验证, 2-已验证, 3-已废弃)
	 */
	private Short status;
	
}

