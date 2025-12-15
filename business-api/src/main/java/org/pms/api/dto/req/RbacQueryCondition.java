package org.pms.api.dto.req;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.pms.api.common.PageRequest;

import java.util.Date;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 用户信息查询条件类
 * @create 2025/12/11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RbacQueryCondition extends PageRequest {
	
	private Date startTime;
	private Date endTime;
	
}
