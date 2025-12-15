package org.pms.api.dto.req;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.pms.api.common.PageRequest;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDataQueryCondition extends PageRequest {

    private String deviceSN;
    private Boolean processState;
    private Date startTime;
    private Date endTime;

}
