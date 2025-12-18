package org.pms.domain.rbac.repository;

import org.pms.domain.rbac.model.command.CreateUserCommand;
import org.pms.domain.rbac.model.command.UpdateUserCommand;
import org.pms.domain.rbac.model.entity.UserEntity;

import java.util.List;

/**
 * 用户仓储接口
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author zeal
 * @author refactor
 * @version 1.0
 * @since 2024/7/1 下午3:03
 */
public interface IUserRepository {

	UserEntity getUserEntityByUsername(String username);

	UserEntity queryUserById(Long id);

	void registerUser(CreateUserCommand command);

	void saveUser(CreateUserCommand command, String operatorName);

	void updateUser(UpdateUserCommand command, String operatorName);
	
	void deleteUserById(String operatorName, Long userId);
	
	List<String> queryAuthoritiesByName(String name);
	
	String queryRoleByName(String username);
	
	String queryRoleByUserId(Long userId);
	
	void resetPasswordById(Long userId, String operatorName, String defaultPassword);
	
	void changePassword(Long userId, String operatorName, String newPassword);
	
	String queryUserPasswordById(Long userId);
	
}
