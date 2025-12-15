package org.pms.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateReq {
	
	// 用户名不能为空
	private String username;
	// 密码不能为空
	private String password;
	private String phone;
	// 角色Id不能为空
	private Long roleId;
	
}
