package org.pms.domain.terminal.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEntity {

    private Long id;
    private Long pipelineId;
    private Long pipelineSN;
    private Long deviceSN;
    private String protocolSN;
    private Integer status;
    private Long templateId;
    private String customerAccount;
    private Integer temperatureUpperBound;
    private Integer temperatureLowerBound;
    private Integer voltageUpperBound;
    private Integer voltageLowerBound;
    private Integer pressureUpperBound;
    private Integer pressureLowerBound;
    private Integer localAtmosphericPressure;
    private String longitude;
    private String latitude;
    private Date createTime;
    private Date updateTime;
    private String updateBy;
    private Date deleteTime;
    private String deleteBy;
    private Boolean binded;
    private Boolean removed;
    private String remark;

}
