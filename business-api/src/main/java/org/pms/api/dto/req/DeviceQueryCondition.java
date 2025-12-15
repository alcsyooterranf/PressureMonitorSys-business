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
public class DeviceQueryCondition extends PageRequest {

    private String protocolSN;
    private Long deviceSN;
    private String customerAccount;
    private Boolean binded;
    private Date startTime;
    private Date endTime;

}
