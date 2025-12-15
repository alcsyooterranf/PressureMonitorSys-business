package org.pms.infrastructure.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * WHAT THE ZZZZEAL
 *
 * @author zeal
 * @version 1.0
 * @since 2024/5/15 下午4:20
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPO {

    private Long id;
    private String username;
    private String password;
    private String phone;
    private Boolean locked;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private Date deleteTime;
    private String deleteBy;
    private Boolean removed;

}