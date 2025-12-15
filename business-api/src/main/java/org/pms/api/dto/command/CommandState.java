package org.pms.api.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author alcsyooterranf
 */

@Getter
@AllArgsConstructor
public enum CommandState {
	
	UNKNOW_ERROR(0, "未知状态错误"),
	INITIALIZED(1, "指令已初始化"),
	SAVED(2, "指令已保存"),
	SENT(3, "指令已发送"),
	DELIVERED(4, "指令已送达"),
	COMPLETED(5, "指令已完成"),
	TTL_TIMEOUT(6, "指令TTL超时"),
	TIMEOUT(7, "指令超时");
	
	private final Integer code;
	private final String desc;
	
}
