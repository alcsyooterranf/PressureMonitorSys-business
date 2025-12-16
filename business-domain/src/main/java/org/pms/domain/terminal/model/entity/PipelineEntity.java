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

public class PipelineEntity {

    private Long id;
    private Long pipelineSN;
    private String pipelineName;
    private String customerAccount;
    private String location;
    private Date createTime;
    private Date updateTime;
    private String updateBy;
    private Date deleteTime;
    private String deleteBy;
    private Boolean removed;
    private String remark;

}
