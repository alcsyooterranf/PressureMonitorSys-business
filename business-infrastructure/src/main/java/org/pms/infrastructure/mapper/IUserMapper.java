package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.pms.api.dto.req.RbacQueryCondition;
import org.pms.infrastructure.mapper.po.UserPO;
import org.pms.infrastructure.mapper.pojo.UserInfo;

import java.util.List;

/**
 * WHAT THE ZZZZEAL
 *
 * @author zeal
 * @version 1.0
 * @since 2024/5/26 下午5:07
 */
@Mapper
public interface IUserMapper {
	
	Long queryUserInfoCount(RbacQueryCondition queryCondition);
	
	List<UserInfo> queryUserInfoList(RbacQueryCondition queryCondition);
	
	void insertUser(String operatorName, UserPO userPO);
	
	int updateUser(String operatorName, UserPO userPO);
	
	int deleteUserById(String operatorName, Long userId);
	
	UserPO selectUserById(Long id);
	
	Long selectUserIdByName(String username);
	
	List<String> selectAuthoritiesByName(String name);
	
	String selectRoleNameByName(String username);
	
	String selectRoleNameByUserId(Long userId);
	
	int updateUserPassword(Long userId, String operatorName, String newPassword);
	
	String selectUserPasswordById(Long userId);
	
	UserPO selectUserByName(String username);
	
}
