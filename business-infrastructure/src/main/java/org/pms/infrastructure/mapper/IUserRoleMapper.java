package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.pms.infrastructure.mapper.po.UserRolePO;

@Mapper
public interface IUserRoleMapper {

    void insertUserRole(UserRolePO userRolePO);

    int updateUserRoleByUserId(UserRolePO userRolePO);

}
