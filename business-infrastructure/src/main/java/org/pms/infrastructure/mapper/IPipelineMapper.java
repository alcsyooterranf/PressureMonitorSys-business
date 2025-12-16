package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.pms.api.dto.req.PipelineQueryCondition;
import org.pms.api.dto.req.PipelineUpdateReq;
import org.pms.infrastructure.mapper.po.PipelinePO;

import java.util.List;

@Mapper
public interface IPipelineMapper {
	
	Long queryPipelineCount(PipelineQueryCondition queryCondition);
	
	List<PipelinePO> queryPipelineList(PipelineQueryCondition queryCondition);
	
	int deletePipelineById(String operatorName, Long id);
	
	List<PipelinePO> queryByIdList(List<Long> ids);
	
	int updateProduct(String operatorName, PipelineUpdateReq pipelineUpdateReq);
	
	int updateLocationById(String operatorName, Long id, String location);
	
	void insertProduct(PipelinePO pipelinePO);
	
	List<Long> queryIdsList();
	
}
