package org.pms.domain.rbac.repository;

import org.pms.domain.rbac.model.entity.UserEntity;
import org.pms.api.dto.req.UserCreateReq;
import org.pms.api.dto.req.UserUpdateReq;

import java.util.List;

/**
 * WHAT THE ZZZZEAL
 *
 * @author zeal
 * @version 1.0
 * @since 2024/7/1 下午3:03
 */
public interface IUserRepository {
	
	UserEntity getUserEntityByUsername(String username);
	
	UserEntity queryUserById(Long id);
	
	void registerUser(UserCreateReq userCreateReq);
	
	void saveUser(UserCreateReq userCreateReq, String operatorName);
	
	void updateUser(UserUpdateReq userUpdateReq, String operatorName);
	
	void deleteUserById(String operatorName, Long userId);
	
	List<String> queryAuthoritiesByName(String name);
	
	String queryRoleByName(String username);
	
	String queryRoleByUserId(Long userId);
	
	void resetPasswordById(Long userId, String operatorName, String defaultPassword);
	
	void changePassword(Long userId, String operatorName, String newPassword);
	
	String queryUserPasswordById(Long userId);
	
}
