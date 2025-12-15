package org.pms.infrastructure.repository.query;

import jakarta.annotation.Resource;
import org.pms.application.query.IRbacQueryService;
import org.pms.api.dto.req.RbacQueryCondition;
import org.pms.api.dto.resp.RoleInfoQueryView;
import org.pms.api.dto.resp.UserInfoQueryView;
import org.pms.infrastructure.adapter.AuthConverter;
import org.pms.infrastructure.mapper.IRoleMapper;
import org.pms.infrastructure.mapper.IUserMapper;
import org.pms.infrastructure.mapper.pojo.UserInfo;
import org.pms.api.common.PageResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 用户信息查询仓储服务
 * @create 2025/12/11
 */
@Repository
public class RbacQueryRepository implements IRbacQueryService {
	
	@Resource
	private AuthConverter authConverter;
	@Resource
	private IUserMapper userMapper;
	@Resource
	private IRoleMapper roleMapper;
	
	@Override
	public PageResponse<UserInfoQueryView> queryUserInfoPage(RbacQueryCondition queryCondition) {
		// 分页查询
		Long count = userMapper.queryUserInfoCount(queryCondition);
		List<UserInfo> userInfos = userMapper.queryUserInfoList(queryCondition);
		
		// 空值提前返回
		if (Objects.equals(0L, count) || userInfos.isEmpty()) {
			return PageResponse.<UserInfoQueryView>builder().count(count).build();
		}
		
		// 对象转换
		List<UserInfoQueryView> convert = authConverter.userInfos2views(userInfos);
		
		return PageResponse.<UserInfoQueryView>builder().count(count).data(convert).build();
	}
	
	@Override
	public PageResponse<RoleInfoQueryView> queryRoleInfoPage(RbacQueryCondition queryCondition) {
		// 分页查询
		Long count = roleMapper.queryRoleCount(queryCondition);
		List<RoleInfoQueryView> roleInfoQueryViews = roleMapper.queryRoleList(queryCondition);
		
		// 空值提前返回
		if (Objects.equals(0L, count) || roleInfoQueryViews.isEmpty()) {
			return PageResponse.<RoleInfoQueryView>builder().count(count).build();
		}
		
		return PageResponse.<RoleInfoQueryView>builder().count(count).data(roleInfoQueryViews).build();
	}
	
}
