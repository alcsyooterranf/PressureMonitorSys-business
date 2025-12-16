package org.pms.domain.rbac.repository;

import org.pms.domain.rbac.model.entity.UserRoleEntity;

public interface IUserRoleRepository {

    void saveUserRole(UserRoleEntity userRoleEntity);

    void updateUserRole(UserRoleEntity userRoleEntity);

}
