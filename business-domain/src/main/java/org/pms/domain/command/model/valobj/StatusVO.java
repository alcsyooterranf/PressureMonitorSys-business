package org.pms.domain.command.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据状态枚举
 * @create 2025/12/15
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StatusVO {
	
	UNVERIFIED((short) 1, "未验证"),
	VERIFIED((short) 2, "已验证"),
	DEPRECATED((short) 3, "已废弃");
	
	private Short code;
	private String desc;
	
}
