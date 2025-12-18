package org.pms.domain.rbac.model.command;

import lombok.Builder;
import lombok.Value;

/**
 * 更新用户命令对象
 * <p>
 * 重构说明：
 * - 替代API层的UserUpdateReq
 * - 使用@Value注解保证不可变性
 * - 符合DDD Command模式
 * - 使用toBuilder=true支持创建修改后的副本
 *
 * @author refactor
 * @date 2025-12-18
 */
@Value
@Builder(toBuilder = true)
public class UpdateUserCommand {
	
	/**
	 * 主键ID（不能为空）
	 */
	Long id;
	
	/**
	 * 用户名
	 */
	String username;
	
	/**
	 * 旧密码
	 */
	String oldPassword;
	
	/**
	 * 新密码
	 */
	String newPassword;
	
	/**
	 * 手机号
	 */
	String phone;
	
	/**
	 * 角色ID
	 */
	Long roleId;
	
}

