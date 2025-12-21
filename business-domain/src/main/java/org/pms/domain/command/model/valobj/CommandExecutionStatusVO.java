package org.pms.domain.command.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令执行状态枚举, 注意枚举的序号code不能乱序！！！
 * @create 2025/12/16
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CommandExecutionStatusVO {
	
	SAVED((short) 0, "指令已保存"),
	SENT((short) 1, "指令已发送"),
	DELIVERED((short) 2, "指令已送达"),
	COMPLETED((short) 3, "指令已完成"),
	TTL_TIMEOUT((short) 4, "指令TTL超时"),
	TIMEOUT((short) 5, "指令超时");
	
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
	
	public static CommandExecutionStatusVO fromExternalResultCode(String external) {
		if (external == null) return null;
		return switch (external) {
			case "SAVED" -> SAVED;
			case "SENT" -> SENT;
			case "DELIVERED" -> DELIVERED;
			case "COMPLETED" -> COMPLETED;
			case "TTL_TIMEOUT" -> TTL_TIMEOUT;
			case "TIMEOUT" -> TIMEOUT;
			default -> null;
		};
	}
	
	/**
	 * 判断是否为终态
	 */
	public boolean isFinalState() {
		return this == COMPLETED || this == TTL_TIMEOUT || this == TIMEOUT;
	}
}

