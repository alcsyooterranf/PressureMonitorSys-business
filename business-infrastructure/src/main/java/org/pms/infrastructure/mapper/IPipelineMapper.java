package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.pms.api.dto.req.PipelineQueryCondition;
import org.pms.domain.terminal.model.command.UpdatePipelineCommand;
import org.pms.infrastructure.mapper.po.PipelinePO;

import java.util.List;

/**
 * 管道Mapper
 * <p>
 * 重构说明：
 * - updatePipeline方法参数从PipelineUpdateReq改为UpdatePipelineCommand
 * - 解除对API层DTO的依赖
 *
 * @author refactor
 * @date 2025-12-18
 */
@Mapper
public interface IPipelineMapper {

	Long queryPipelineCount(PipelineQueryCondition queryCondition);

	List<PipelinePO> queryPipelineList(PipelineQueryCondition queryCondition);

	int deletePipelineById(String operatorName, Long id);

	List<PipelinePO> queryByIdList(List<Long> ids);

	int updatePipeline(String operatorName, UpdatePipelineCommand command);
	
	int updateLocationById(String operatorName, Long id, String location);
	
	void insertPipeline(PipelinePO pipelinePO);
	
	List<Long> queryIdsList();
	
}
