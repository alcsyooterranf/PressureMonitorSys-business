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
 * @description 指令元数据查询视图
 * @create 2025/12/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandMetaQueryView {

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
	 * 版本号
	 */
	private Integer version;

	/**
	 * payload schema (JSON字符串)
	 */
	private String payloadSchema;

	/**
	 * 状态 (1-未验证, 2-已验证, 3-已废弃)
	 */
	private Short status;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

}

