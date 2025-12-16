package org.pms.domain.rbac.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    private Long id;
    private String username;
    private transient String password;
    private String phone;
    private Boolean locked;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private Date deleteTime;
    private String deleteBy;

}
