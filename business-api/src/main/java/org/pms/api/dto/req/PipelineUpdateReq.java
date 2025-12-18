package org.pms.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineUpdateReq {
	
	// 主键不能为空
	private Long id;
	private String pipelineName;
	private String location;
	private String longitude;
	private String latitude;
	private String remark;
	
}
