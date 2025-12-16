package org.pms.domain.command.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令执行状态枚举
 * @create 2025/12/16
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CommandExecutionStatusVO {
	
	INITIALIZED((short) 1, "已初始化"),
	SAVED((short) 2, "已保存"),
	SENT((short) 3, "已发送"),
	DELIVERED((short) 4, "已送达"),
	COMPLETED((short) 5, "已完成"),
	TTL_TIMEOUT((short) 6, "TTL超时"),
	TIMEOUT((short) 7, "超时");
	
	private Short code;
	private String desc;
	
	/**
	 * 根据code获取枚举
	 */
	public static CommandExecutionStatusVO fromCode(Short code) {
		if (code == null) {
			return null;
		}
		for (CommandExecutionStatusVO status : values()) {
			if (status.getCode().equals(code)) {
				return status;
			}
		}
		return null;
	}
	
	/**
	 * 判断是否为终态
	 */
	public boolean isFinalState() {
		return this == COMPLETED || this == TTL_TIMEOUT || this == TIMEOUT;
	}
	
}

