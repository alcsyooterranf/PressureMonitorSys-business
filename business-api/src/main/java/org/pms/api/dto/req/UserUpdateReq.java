package org.pms.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateReq {
	
	// 主键不能为空
	private Long id;
	private String username;
	private String oldPassword;
	private String newPassword;
	private String phone;
	private Long roleId;
	
}
