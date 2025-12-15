package org.pms.api.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pms.api.common.HttpResponse;
import org.pms.api.common.PageResponse;
import org.pms.api.dto.req.RbacQueryCondition;
import org.pms.api.dto.req.UserCreateReq;
import org.pms.api.dto.req.UserUpdateReq;
import org.pms.api.dto.resp.RoleInfoQueryView;
import org.pms.api.dto.resp.UserInfoQueryView;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 用户管理外部接口
 * @create 2025/12/13
 */
public interface IUserManagerFacade {
	
	HttpResponse<PageResponse<UserInfoQueryView>> queryUserByConditions(RbacQueryCondition request);
	
	HttpResponse<PageResponse<RoleInfoQueryView>> queryRoleByConditions(RbacQueryCondition request);
	
	HttpResponse<String> registerUser(UserCreateReq userCreateReq);
	
	HttpResponse<String> createUser(String securityContextEncoded, UserCreateReq userCreateReq) throws JsonProcessingException;
	
	HttpResponse<String> updateUser(String securityContextEncoded, UserUpdateReq userUpdateReq) throws JsonProcessingException;
	
	HttpResponse<String> deleteUser(String securityContextEncoded, Long userId) throws JsonProcessingException;
	
	HttpResponse<String> resetPassword(String securityContextEncoded, Long userId) throws JsonProcessingException;
	
}

