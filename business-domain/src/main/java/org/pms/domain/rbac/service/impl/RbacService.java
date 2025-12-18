package org.pms.domain.rbac.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pms.types.BizCode;
import org.pms.types.BizConstants;
import org.pms.types.exception.BizException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pms.domain.rbac.model.command.CreateUserCommand;
import org.pms.domain.rbac.model.command.UpdateUserCommand;
import org.pms.domain.rbac.model.entity.UserEntity;
import org.pms.domain.rbac.model.entity.UserRoleEntity;
import org.pms.domain.rbac.model.req.SecurityContextHeader;
import org.pms.domain.rbac.repository.IUserRepository;
import org.pms.domain.rbac.repository.IUserRoleRepository;
import org.pms.domain.rbac.service.IRbacService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * RBAC服务实现
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 * - 使用BizException和BizCode替代AppException和ResponseCode
 *
 * @author zeal
 * @author refactor
 * @version 1.0
 * @since 2024/10/31 上午10:45
 */
@Slf4j
@Service
public class RbacService implements IRbacService {

	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	@Resource
	private IUserRepository userRepository;
	@Resource
	private IUserRoleRepository userRoleRepository;

	@Override
	public void registerUser(CreateUserCommand command) {
		String newPassword = command.getPassword();
		if (StringUtils.isBlank(newPassword)) {
			throw new BizException(BizCode.PASSWORD_IS_EMPTY);
		}
		newPassword = encoder.encode(command.getPassword());
		CreateUserCommand encodedCommand = command.toBuilder().password(newPassword).build();
		userRepository.registerUser(encodedCommand);
	}

	@Override
	public void createUser(CreateUserCommand command, String securityContextEncoded) throws JsonProcessingException {
		String newPassword = command.getPassword();
		if (StringUtils.isBlank(newPassword)) {
			throw new BizException(BizCode.PASSWORD_IS_EMPTY);
		}
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		newPassword = encoder.encode(command.getPassword());
		CreateUserCommand encodedCommand = command.toBuilder().password(newPassword).build();
		userRepository.saveUser(encodedCommand, securityContext.getUsername());
	}
	
	@Override
	public void deleteUserById(Long userId, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		String adminName = securityContext.getUsername();
		userRepository.deleteUserById(adminName, userId);
	}
	
	@Override
	public void changePassword(String oldPassword, String newPassword, String securityContextEncoded) throws JsonProcessingException {
		// 0. 检查新密码与旧密码是否一致
		if (StringUtils.equals(oldPassword, newPassword)) {
			throw new BizException(BizCode.PASSWORD_IS_SAME);
		}

		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		Long id = securityContext.getId();

		// 1.从数据库中查询用户的原始密码
		String password = userRepository.queryUserPasswordById(id);

		// 2. 检验密码是否为空
		if (StringUtils.isBlank(newPassword) || StringUtils.isBlank(oldPassword) || StringUtils.isBlank(password)) {
			throw new BizException(BizCode.PASSWORD_IS_EMPTY);
		}

		// 3. 检验原始密码是否一致
		if (!encoder.matches(oldPassword, password)) {
			throw new BizException(BizCode.OLD_PASSWORD_ERROR);
		}

		String userName = securityContext.getUsername();
		newPassword = encoder.encode(newPassword);
		userRepository.changePassword(id, userName, newPassword);
	}
	
	@Override
	public void resetPassword(Long userId, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		String adminName = securityContext.getUsername();
		String encode = encoder.encode(BizConstants.DEFAULT_PASSWORD);
		userRepository.resetPasswordById(userId, adminName, encode);
	}
	
	@Override
	public void changeUserRole(Long userId, Long newRoleId) {
		// TODO: 目前规定一个用户只能同时拥有一个角色, 所以是更新方法, 而不是插入方法
		// 1. 先查询要修改的用户的角色信息
		String roleName = userRepository.queryRoleByUserId(userId);
		if (Objects.equals(roleName, "admin")) {
			throw new BizException(BizCode.INSUFFICIENT_PERMISSIONS);
		}
		// 2. 再对权限进行更改
		UserRoleEntity userRoleEntity = UserRoleEntity.builder()
				.userId(userId)
				.roleId(newRoleId)
				.build();
		userRoleRepository.updateUserRole(userRoleEntity);
	}
	
	@Override
	public UserEntity selectUserById(Long id) {
		return userRepository.queryUserById(id);
	}
	
	@Override
	public UserEntity selectUserByName(String username) {
		return userRepository.getUserEntityByUsername(username);
	}
	
	@Override
	public void updateUser(UpdateUserCommand command, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		String adminName = securityContext.getUsername();
		String oldPassword = command.getOldPassword();
		String newPassword = command.getNewPassword();
		// 密码检查
		// 0. 检查非法密码输入
		if (StringUtils.isBlank(oldPassword) && StringUtils.isNotBlank(newPassword) || StringUtils.isNotBlank(oldPassword) && StringUtils.isBlank(newPassword)) {
			throw new BizException(BizCode.PASSWORD_IS_EMPTY);
		}
		// 1. 检查用户是否要修改密码, 即新旧密码是否为空
		if (StringUtils.isBlank(oldPassword) && StringUtils.isBlank(newPassword)) {
			// 不修改密码
			userRepository.updateUser(command, adminName);
		} else {
			// 修改密码, 此时新旧密码均存在且不为空
			// 0.从数据库中查询用户的原始密码
			Long id = command.getId();
			String password = userRepository.queryUserPasswordById(id);
			// 1. 检验旧密码与原始密码是否一致
			if (!encoder.matches(oldPassword, password)) {
				throw new BizException(BizCode.OLD_PASSWORD_ERROR);
			}
			// 2. 检查新密码与原始密码是否一致
			if (StringUtils.equals(oldPassword, newPassword)) {
				throw new BizException(BizCode.PASSWORD_IS_SAME);
			}

			newPassword = encoder.encode(newPassword);
			UpdateUserCommand encodedCommand = command.toBuilder().newPassword(newPassword).build();
			userRepository.updateUser(encodedCommand, adminName);
		}
	}
	
}
