package org.pms.infrastructure.adapter.repository.persistence;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.rbac.model.command.CreateUserCommand;
import org.pms.domain.rbac.model.command.UpdateUserCommand;
import org.pms.domain.rbac.model.entity.UserEntity;
import org.pms.domain.rbac.repository.IUserRepository;
import org.pms.infrastructure.adapter.converter.AuthConverter;
import org.pms.infrastructure.mapper.IUserMapper;
import org.pms.infrastructure.mapper.IUserRoleMapper;
import org.pms.infrastructure.mapper.po.UserPO;
import org.pms.infrastructure.mapper.po.UserRolePO;
import org.pms.types.BizCode;
import org.pms.types.BizConstants;
import org.pms.types.BizException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * WHAT THE ZZZZEAL
 *
 * @author zeal
 * @version 1.0
 * @since 2024/7/1 下午9:14
 */
@Slf4j
@Repository
public class UserRepository implements IUserRepository {
	
	private static final Long DEFAULT_ROLE_ID = BizConstants.DEFAULT_ROLE_ID;
	@Resource
	private IUserMapper userMapper;
	@Resource
	private IUserRoleMapper userRoleMapper;
	@Resource
	private AuthConverter authConverter;
	@Resource
	private TransactionTemplate transactionTemplate;
	
	@Override
	public UserEntity getUserEntityByUsername(String username) {
		UserPO userpo = userMapper.selectUserByName(username);
		return authConverter.userPO2Entity(userpo);
	}
	
	@Override
	public void registerUser(CreateUserCommand command) {
		UserPO userPO = authConverter.createUserCommand2PO(command);
		transactionTemplate.execute(status -> {
			try {
				// 1. 保存用户信息, 并返回主键id
				userMapper.insertUser(command.getUsername(), userPO);
				UserRolePO userRolePO = UserRolePO.builder()
						.userId(userPO.getId())
						.roleId(DEFAULT_ROLE_ID)
						.build();
				// 2. 保存用户角色 r_user_role
				userRoleMapper.insertUserRole(userRolePO);
			} catch (Exception e) {
				status.setRollbackOnly();
				throw e;
			}
			return 1;
		});
	}
	
	@Override
	public void saveUser(CreateUserCommand command, String operatorName) {
		UserPO userPO = authConverter.createUserCommand2PO(command);
		transactionTemplate.execute(status -> {
			try {
				// 1. 保存用户信息, 并返回主键id
				userMapper.insertUser(operatorName, userPO);
				UserRolePO userRolePO = UserRolePO.builder()
						.userId(userPO.getId())
						.roleId(command.getRoleId())
						.build();
				// 2. 保存用户角色 r_user_role
				userRoleMapper.insertUserRole(userRolePO);
			} catch (Exception e) {
				status.setRollbackOnly();
				throw e;
			}
			return 1;
		});
	}
	
	@Override
	public void updateUser(UpdateUserCommand command, String operatorName) {
		// TODO: 原子锁改写
		UserPO userPO = authConverter.updateUserCommand2PO(command);
		// roleId不为空(管理员修改), 开启事务, 需更新user表和user_role表信息
		if (command.getRoleId() != null) {
			transactionTemplate.execute(status -> {
				try {
					// 1.先更新用户基本信息
					int updateCnt = userMapper.updateUser(operatorName, userPO);
					if (1 != updateCnt) {
						// 未更新成功则回滚
						status.setRollbackOnly();
						throw new BizException(BizCode.USER_ID_ERROR);
					}
					// 2.再更新用户-角色信息
					UserRolePO userRolePO = UserRolePO.builder()
							.userId(command.getId())
							.roleId(command.getRoleId())
							.build();
					updateCnt = userRoleMapper.updateUserRoleByUserId(userRolePO);
					if (1 != updateCnt) {
						// 未更新成功则回滚
						status.setRollbackOnly();
						throw new BizException(BizCode.USER_ID_OR_ROLE_ID_ERROR);
					}
				} catch (DuplicateKeyException e) {
					status.setRollbackOnly();
					throw e;
				}
				return 1;
			});
		}
		// roleId为空(用户修改), 不开启事务, 只更新user表
		else {
			try {
				int updateCnt = userMapper.updateUser(operatorName, userPO);
				if (1 != updateCnt) {
					throw new BizException(BizCode.USER_ID_ERROR);
				}
			} catch (DuplicateKeyException e) {
				throw e;
			}
		}
	}
	
	@Override
	public void deleteUserById(String operatorName, Long userId) {
		int updateCnt = userMapper.deleteUserById(operatorName, userId);
		if (1 != updateCnt) {
			throw new BizException(BizCode.USER_ID_ERROR);
		}
	}
	
	@Override
	public List<String> queryAuthoritiesByName(String name) {
		return userMapper.selectAuthoritiesByName(name);
	}
	
	@Override
	public String queryRoleByName(String username) {
		return userMapper.selectRoleNameByName(username);
	}
	
	@Override
	public String queryRoleByUserId(Long userId) {
		return userMapper.selectRoleNameByUserId(userId);
	}
	
	@Override
	public void resetPasswordById(Long userId, String operatorName, String defaultPassword) {
		int updateCnt = userMapper.updateUserPassword(userId, operatorName, defaultPassword);
		if (1 != updateCnt) {
			throw new BizException(BizCode.USER_ID_ERROR);
		}
	}
	
	@Override
	public void changePassword(Long userId, String operatorName, String newPassword) {
		int updateCnt = userMapper.updateUserPassword(userId, operatorName, newPassword);
		if (1 != updateCnt) {
			throw new BizException(BizCode.USER_ID_ERROR);
		}
	}
	
	@Override
	public String queryUserPasswordById(Long userId) {
		return userMapper.selectUserPasswordById(userId);
	}
	
	@Override
	public UserEntity queryUserById(Long id) {
		UserPO userPO = userMapper.selectUserById(id);
		return authConverter.userPO2Entity(userPO);
	}
	
}
