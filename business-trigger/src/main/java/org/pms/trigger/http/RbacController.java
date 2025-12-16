package org.pms.trigger.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pms.types.Constants;
import com.pms.types.ResponseCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.common.HttpResponse;
import org.pms.api.common.PageResponse;
import org.pms.api.dto.req.RbacQueryCondition;
import org.pms.api.dto.req.UserCreateReq;
import org.pms.api.dto.req.UserUpdateReq;
import org.pms.api.dto.resp.RoleInfoQueryView;
import org.pms.api.dto.resp.UserInfoQueryView;
import org.pms.api.facade.IUserManagerFacade;
import org.pms.application.query.IRbacQueryService;
import org.pms.domain.rbac.service.IRbacService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/rbac/")
public class RbacController implements IUserManagerFacade {
	
	private static final String SECURITY_CONTEXT_HEADER = Constants.SECURITY_CONTEXT_HEADER;
	@Resource
	private IRbacService userService;
	@Resource
	private IRbacQueryService rbacQueryService;
	
	@RequestMapping(value = "user/query", method = RequestMethod.POST)
	public HttpResponse<PageResponse<UserInfoQueryView>> queryUserByConditions(@RequestBody RbacQueryCondition request) {
		log.info("管理员查询用户列表");
		PageResponse<UserInfoQueryView> userInfoViewPage = rbacQueryService.queryUserInfoPage(request);
		
		return HttpResponse.<PageResponse<UserInfoQueryView>>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.data(userInfoViewPage)
				.build();
	}
	
	@RequestMapping(value = "role/query", method = RequestMethod.POST)
	public HttpResponse<PageResponse<RoleInfoQueryView>> queryRoleByConditions(@RequestBody RbacQueryCondition request) {
		log.info("管理员查询角色列表");
		PageResponse<RoleInfoQueryView> roleInfoViewPage = rbacQueryService.queryRoleInfoPage(request);
		
		return HttpResponse.<PageResponse<RoleInfoQueryView>>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.data(roleInfoViewPage)
				.build();
	}
	
	@RequestMapping(value = "user/register", method = RequestMethod.POST)
	public HttpResponse<String> registerUser(@RequestBody UserCreateReq userCreateReq) {
		log.info("注册用户 {} ", userCreateReq);
		userService.registerUser(userCreateReq);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	@RequestMapping(value = "user/create", method = RequestMethod.POST)
	public HttpResponse<String> createUser(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
										   @RequestBody UserCreateReq userCreateReq) throws JsonProcessingException {
		log.info("管理员创建用户 {}", userCreateReq);
		userService.createUser(userCreateReq, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	// 根据userUpdateReq中有无roleId字段来判断发送请求的用户是否为管理员
	@RequestMapping(value = "user/update", method = RequestMethod.POST)
	public HttpResponse<String> updateUser(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
										   @RequestBody UserUpdateReq userUpdateReq) throws JsonProcessingException {
		log.info("(管理员)更改用户信息 {}", userUpdateReq);
		userService.updateUser(userUpdateReq, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	@RequestMapping(value = "user/delete", method = RequestMethod.POST)
	public HttpResponse<String> deleteUser(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
										   @RequestParam Long userId) throws JsonProcessingException {
		log.info("管理员根据ID删除用户, id: {}", userId);
		userService.deleteUserById(userId, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	@RequestMapping(value = "user/reset_password", method = RequestMethod.POST)
	public HttpResponse<String> resetPassword(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
											  @RequestParam Long userId) throws JsonProcessingException {
		log.info("管理员根据ID重置用户密码, id: {}", userId);
		userService.resetPassword(userId, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	@Deprecated
	@RequestMapping(value = "user/change_password", method = RequestMethod.POST)
	public HttpResponse<String> changePassword(@RequestHeader(SECURITY_CONTEXT_HEADER) String securityContextEncoded,
											   @RequestParam String oldPassword, @RequestParam String newPassword) throws JsonProcessingException {
		log.info("用户修改密码");
		userService.changePassword(oldPassword, newPassword, securityContextEncoded);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
	@Deprecated
	@RequestMapping(value = "role/change", method = RequestMethod.POST)
	public HttpResponse<String> changeUserRole(@RequestParam Long userId, @RequestParam Long newRoleId) {
		// TODO: 修改用户角色, 注意处理唯一索引冲突异常
		// TODO: 目前只支持管理员修改比它级别低的角色, 不支持同级角色项目修改角色
		log.info("管理员修改用户角色");
		userService.changeUserRole(userId, newRoleId);
		return HttpResponse.<String>builder()
				.code(ResponseCode.SUCCESS.getCode())
				.message(ResponseCode.SUCCESS.getMessage())
				.build();
	}
	
}
