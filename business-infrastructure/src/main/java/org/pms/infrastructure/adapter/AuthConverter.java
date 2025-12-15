package org.pms.infrastructure.adapter;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.pms.api.dto.resp.UserInfoQueryView;
import org.pms.domain.auth.model.entity.RoleEntity;
import org.pms.domain.auth.model.entity.UserEntity;
import org.pms.domain.auth.model.entity.UserRoleEntity;
import org.pms.api.dto.req.UserCreateReq;
import org.pms.api.dto.req.UserUpdateReq;
import org.pms.infrastructure.mapper.po.RolePO;
import org.pms.infrastructure.mapper.po.UserPO;
import org.pms.infrastructure.mapper.po.UserRolePO;
import org.pms.infrastructure.mapper.pojo.UserInfo;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AuthConverter {
	
	/**
	 * 单个JavaBean转换 UserCreateReq -> UserPO
	 *
	 * @param userCreateReq Req类
	 * @return PO类
	 */
	public abstract UserPO userCreateReq2PO(UserCreateReq userCreateReq);
	
	/**
	 * 单个JavaBean转换 UserUpdateReq -> UserPO
	 *
	 * @param userUpdateReq Req类
	 * @return PO类
	 */
	@Mapping(target = "password", source = "newPassword")
	public abstract UserPO userUpdateReq2PO(UserUpdateReq userUpdateReq);
	
	/**
	 * 单个JavaBean转换 UserPO -> UserEntity
	 *
	 * @param userPO PO类
	 * @return Entity类
	 */
	public abstract UserEntity userPO2Entity(UserPO userPO);
	
	/**
	 * 批量JavaBean转换 List<UserRoleDTO> -> List<UserQueryRes>
	 *
	 * @param userInfos 类列表
	 * @return List<UserInfoQueryView>
	 */
	public abstract List<UserInfoQueryView> userRoleDTOs2Res(List<UserInfo> userInfos);
	
	/**
	 * 单个JavaBean转换 UserEntity -> UserPO
	 *
	 * @param userEntity Entity
	 * @return UserPO
	 */
	@InheritInverseConfiguration(name = "userPO2Entity")
	public abstract UserPO userEntity2PO(UserEntity userEntity);
	
	/**
	 * 单个JavaBean转换 UserRoleEntity -> UserRolePO
	 *
	 * @param userRoleEntity Entity
	 * @return UserRolePO
	 */
	public abstract UserRolePO userRoleEntity2PO(UserRoleEntity userRoleEntity);
	
	/**
	 * 单个JavaBean转换 RolePO -> RoleEntity
	 *
	 * @param rolePO PO
	 * @return RoleEntity
	 */
	public abstract RoleEntity rolePO2Entity(RolePO rolePO);
	
	/**
	 * 批量JavaBean转换 List<RolePO> -> List<RoleEntity>
	 *
	 * @param rolePOs 类列表
	 * @return List<RoleEntity>
	 */
	public abstract List<RoleEntity> rolePOs2entities(List<RolePO> rolePOs);
	
	/**
	 * 单个JavaBean转换 UserInfo -> UserInfoQueryView
	 *
	 * @param userInfo PO
	 * @return UserInfoQueryView
	 */
	public abstract UserInfoQueryView userInfo2View(UserInfo userInfo);
	
	/**
	 * 批量JavaBean转换 List<UserInfo> -> List<UserInfoQueryView>
	 *
	 * @param userInfos 类列表
	 * @return List<UserInfoQueryView>
	 */
	public abstract List<UserInfoQueryView> userInfos2views(List<UserInfo> userInfos);
	
}
