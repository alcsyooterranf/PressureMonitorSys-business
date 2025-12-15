package org.pms.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineInsertReq {

    private Long productSN;
    private String productName;
    private String customerAccount;
    private String location;
    private String remark;

}
