package org.pms.domain.auth.repository;

import org.pms.domain.auth.model.entity.UserRoleEntity;

public interface IUserRoleRepository {

    void saveUserRole(UserRoleEntity userRoleEntity);

    void updateUserRole(UserRoleEntity userRoleEntity);

}
