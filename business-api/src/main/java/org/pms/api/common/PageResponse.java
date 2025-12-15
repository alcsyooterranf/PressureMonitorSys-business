package org.pms.api.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 分页返回通用类
 * @create 2025/12/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
	
	/**
	 * 总记录数
	 */
	
	private Long count;
	
	/**
	 * 数据列表
	 */
	private List<T> data;
	
}
