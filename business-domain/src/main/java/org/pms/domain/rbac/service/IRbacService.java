package org.pms.domain.rbac.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pms.domain.rbac.model.entity.UserEntity;
import org.pms.api.dto.req.UserCreateReq;
import org.pms.api.dto.req.UserUpdateReq;

public interface IRbacService {
	
	void registerUser(UserCreateReq userCreateReq);
	
	void createUser(UserCreateReq userCreateReq, String securityContextEncoded) throws JsonProcessingException;
	
	void deleteUserById(Long userId, String securityContextEncoded) throws JsonProcessingException;
	
	void resetPassword(Long userId, String securityContextEncoded) throws JsonProcessingException;
	
	void changePassword(String oldPassword, String newPassword, String securityContextEncoded) throws JsonProcessingException;
	
	void changeUserRole(Long userId, Long newRoleId);
	
	UserEntity selectUserById(Long id);
	
	UserEntity selectUserByName(String username);
	
	void updateUser(UserUpdateReq userUpdateReq, String securityContextEncoded) throws JsonProcessingException;
	
}
