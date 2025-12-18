package org.pms.api.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pms.api.common.PageResponse;
import org.pms.api.dto.req.RbacQueryCondition;
import org.pms.api.dto.req.UserCreateReq;
import org.pms.api.dto.req.UserUpdateReq;
import org.pms.api.dto.resp.RoleInfoQueryView;
import org.pms.api.dto.resp.UserInfoQueryView;
import org.pms.types.Response;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 用户管理外部接口
 * @create 2025/12/13
 */
public interface IUserManagerFacade {
	
	Response<PageResponse<UserInfoQueryView>> queryUserByConditions(RbacQueryCondition request);
	
	Response<PageResponse<RoleInfoQueryView>> queryRoleByConditions(RbacQueryCondition request);
	
	Response<String> registerUser(UserCreateReq userCreateReq);
	
	Response<String> createUser(String securityContextEncoded, UserCreateReq userCreateReq) throws JsonProcessingException;
	
	Response<String> updateUser(String securityContextEncoded, UserUpdateReq userUpdateReq) throws JsonProcessingException;
	
	Response<String> deleteUser(String securityContextEncoded, Long userId) throws JsonProcessingException;
	
	Response<String> resetPassword(String securityContextEncoded, Long userId) throws JsonProcessingException;
	
}

