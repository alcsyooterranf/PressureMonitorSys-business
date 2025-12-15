package org.pms.application.query;

import org.pms.api.dto.req.RbacQueryCondition;
import org.pms.api.dto.resp.RoleInfoQueryView;
import org.pms.api.dto.resp.UserInfoQueryView;
import org.pms.api.common.PageResponse;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 用户信息查询仓储接口
 * @create 2025/12/11
 */
public interface IRbacQueryService {
	
	PageResponse<UserInfoQueryView> queryUserInfoPage(RbacQueryCondition queryCondition);
	
	PageResponse<RoleInfoQueryView> queryRoleInfoPage(RbacQueryCondition queryCondition);
	
}
