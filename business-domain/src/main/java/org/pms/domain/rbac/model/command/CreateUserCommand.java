package org.pms.domain.rbac.model.command;

import lombok.Builder;
import lombok.Value;

/**
 * 创建用户命令对象
 * <p>
 * 重构说明：
 * - 替代API层的UserCreateReq
 * - 使用@Value注解保证不可变性
 * - 符合DDD Command模式
 * - 使用toBuilder=true支持创建修改后的副本
 *
 * @author refactor
 * @date 2025-12-18
 */
@Value
@Builder(toBuilder = true)
public class CreateUserCommand {
	
	/**
	 * 用户名（不能为空）
	 */
	String username;
	
	/**
	 * 密码（不能为空）
	 */
	String password;
	
	/**
	 * 手机号
	 */
	String phone;
	
	/**
	 * 角色ID（不能为空）
	 */
	Long roleId;
	
}

