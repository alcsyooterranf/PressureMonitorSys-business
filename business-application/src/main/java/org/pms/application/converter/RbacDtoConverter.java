package org.pms.application.converter;

import org.pms.api.dto.req.UserCreateReq;
import org.pms.api.dto.req.UserUpdateReq;
import org.pms.domain.rbac.model.command.CreateUserCommand;
import org.pms.domain.rbac.model.command.UpdateUserCommand;
import org.springframework.stereotype.Component;

/**
 * Rbac模块DTO转换器
 * <p>
 * 职责：将API层的DTO对象转换为Domain层的Command对象
 * <p>
 * 转换关系：
 * - UserCreateReq → CreateUserCommand
 * - UserUpdateReq → UpdateUserCommand
 *
 * @author refactor
 * @date 2025-12-18
 */
@Component
public class RbacDtoConverter {

    /**
     * UserCreateReq → CreateUserCommand
     */
    public CreateUserCommand toCreateCommand(UserCreateReq req) {
        return CreateUserCommand.builder()
                .username(req.getUsername())
                .password(req.getPassword())
                .phone(req.getPhone())
                .roleId(req.getRoleId())
                .build();
    }

    /**
     * UserUpdateReq → UpdateUserCommand
     */
    public UpdateUserCommand toUpdateCommand(UserUpdateReq req) {
        return UpdateUserCommand.builder()
                .id(req.getId())
                .username(req.getUsername())
                .oldPassword(req.getOldPassword())
                .newPassword(req.getNewPassword())
                .phone(req.getPhone())
                .roleId(req.getRoleId())
                .build();
    }

}

