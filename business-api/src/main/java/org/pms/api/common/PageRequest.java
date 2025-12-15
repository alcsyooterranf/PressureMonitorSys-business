package org.pms.api.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @description: 分页类
 * @author: alcsyooterranf
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
	
	/**
	 * 开始 limit 第1个入参
	 */
	private int pageBegin = 0;
	
	/**
	 * 开始 limit 第2个入参
	 */
	private int pageEnd = 0;
	
	/**
	 * 页数
	 */
	private int page;
	
	/**
	 * 行数
	 */
	private int rows;
	
	public void setPage(String page, String rows) {
		this.page = null == page ? 1 : Integer.parseInt(page);
		this.rows = null == page ? 10 : Integer.parseInt(rows);
		if (0 == this.page) {
			this.page = 1;
		}
		this.pageBegin = (this.page - 1) * this.rows;
		this.pageEnd = this.rows;
	}
	
	public void setPage(int page, int rows) {
		this.page = page;
		this.rows = rows;
		
		if (0 == this.page) {
			this.page = 1;
		}
		this.pageBegin = (this.page - 1) * this.rows;
		this.pageEnd = this.rows;
	}
	
}
