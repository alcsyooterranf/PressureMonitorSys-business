package org.pms.api.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 角色查询仓储前端视图
 * @create 2025/12/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleInfoQueryView {

    private Long id;
    private String name;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;

}
