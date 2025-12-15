package org.pms.infrastructure.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 映射r_user表和r_role表联表查询的结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private Long id;
    private String username;
    private String phone;
    private Long roleId;
    private String roleName;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private Date deleteTime;
    private String deleteBy;

}
