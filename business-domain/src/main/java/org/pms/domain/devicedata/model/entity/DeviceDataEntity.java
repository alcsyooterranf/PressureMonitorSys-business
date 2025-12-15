package org.pms.domain.devicedata.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pms.domain.devicedata.model.vo.AbnormalFlagVO;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDataEntity {

    private Long id;
    private String tenantId;
    private Long pipelineId;
    private String pipelineSN;
    private Integer temperature;
    private Integer voltage;
    private Integer pressure;
    private AbnormalFlagVO abnormalFlagVO;
    private Long deviceId;
    private String deviceSN;
    private Date createTime;
    private Date processTime;
    private String processBy;
    private Date deleteTime;
    private String deleteBy;
    private Boolean removed;
    private Boolean processState;

}
