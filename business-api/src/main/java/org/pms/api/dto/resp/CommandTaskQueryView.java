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
 * @description 指令任务查询视图
 * @create 2025/12/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandTaskQueryView {
	
	/**
	 * 主键ID
	 */
	private Long id;
	
	/**
	 * 租户ID
	 */
	private String tenantId;
	
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
	 * 指令参数 (JSON字符串)
	 */
	private String args;
	
	/**
	 * 创建人
	 */
	private String createBy;
	
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

