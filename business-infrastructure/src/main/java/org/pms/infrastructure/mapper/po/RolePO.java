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
 * @since 2024/6/24 下午8:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePO {

    private Long id;
    private String name;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private Date deleteTime;
    private String deleteBy;
    private Boolean removed;

}
