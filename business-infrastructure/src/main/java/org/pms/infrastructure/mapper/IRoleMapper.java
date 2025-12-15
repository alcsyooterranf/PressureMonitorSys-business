package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.pms.api.dto.req.RbacQueryCondition;
import org.pms.api.dto.resp.RoleInfoQueryView;

import java.util.List;

@Mapper
public interface IRoleMapper {
	
	Long queryRoleCount(RbacQueryCondition queryCondition);
	
	List<RoleInfoQueryView> queryRoleList(RbacQueryCondition queryCondition);
	
}
