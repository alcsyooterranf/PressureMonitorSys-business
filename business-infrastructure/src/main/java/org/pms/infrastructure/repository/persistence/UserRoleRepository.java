package org.pms.infrastructure.repository.persistence;

import com.pms.types.AppException;
import com.pms.types.ResponseCode;
import org.pms.domain.rbac.model.entity.UserRoleEntity;
import org.pms.domain.rbac.repository.IUserRoleRepository;
import org.pms.infrastructure.adapter.AuthConverter;
import org.pms.infrastructure.mapper.IUserRoleMapper;
import org.pms.infrastructure.mapper.po.UserRolePO;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleRepository implements IUserRoleRepository {

    private final IUserRoleMapper userRoleMapper;
    private final AuthConverter authConverter;

    public UserRoleRepository(IUserRoleMapper userRoleMapper, AuthConverter authConverter) {
        this.userRoleMapper = userRoleMapper;
        this.authConverter = authConverter;
    }

    @Override
    public void saveUserRole(UserRoleEntity userRoleEntity) {
        UserRolePO userRolePO = authConverter.userRoleEntity2PO(userRoleEntity);
        userRoleMapper.insertUserRole(userRolePO);
    }

    @Override
    public void updateUserRole(UserRoleEntity userRoleEntity) {
        UserRolePO userRolePO = authConverter.userRoleEntity2PO(userRoleEntity);
        int updateCnt = userRoleMapper.updateUserRoleByUserId(userRolePO);
        if (1 != updateCnt) {
            throw new AppException(ResponseCode.USER_ROLE_ID_ERROR);
        }
    }

}
