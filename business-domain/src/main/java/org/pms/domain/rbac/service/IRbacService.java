package org.pms.domain.rbac.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pms.domain.rbac.model.command.CreateUserCommand;
import org.pms.domain.rbac.model.command.UpdateUserCommand;
import org.pms.domain.rbac.model.entity.UserEntity;

/**
 * RBAC服务接口
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author refactor
 * @date 2025-12-18
 */
public interface IRbacService {

	void registerUser(CreateUserCommand command);

	void createUser(CreateUserCommand command, String securityContextEncoded) throws JsonProcessingException;

	void deleteUserById(Long userId, String securityContextEncoded) throws JsonProcessingException;

	void resetPassword(Long userId, String securityContextEncoded) throws JsonProcessingException;

	void changePassword(String oldPassword, String newPassword, String securityContextEncoded) throws JsonProcessingException;

	void changeUserRole(Long userId, Long newRoleId);

	UserEntity selectUserById(Long id);

	UserEntity selectUserByName(String username);

	void updateUser(UpdateUserCommand command, String securityContextEncoded) throws JsonProcessingException;

}
