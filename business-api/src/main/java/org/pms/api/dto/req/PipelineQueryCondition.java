package org.pms.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.pms.api.common.PageRequest;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineQueryCondition extends PageRequest {
	
	private Long pipelineSN;
	private String customerAccount;
	private Date startTime;
	private Date endTime;
	
}
