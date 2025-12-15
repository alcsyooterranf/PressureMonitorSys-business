package org.pms.infrastructure.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRolePO {

    private Long id;
    private Long userId;
    private Long roleId;

}
