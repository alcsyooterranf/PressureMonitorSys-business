package org.pms.domain.terminal.repository;

import org.pms.domain.terminal.model.command.CreatePipelineCommand;
import org.pms.domain.terminal.model.command.UpdatePipelineCommand;

/**
 * 管道仓储接口
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author refactor
 * @date 2025-12-18
 */
public interface IPipelineRepository {

	/**
	 * 逻辑删除管道
	 *
	 * @param id 管道id
	 * @param operatorName 操作人
	 */
	void deletePipelineById(Long id, String operatorName);

	/**
	 * 根据管道ID更新管道信息
	 *
	 * @param command 更新管道命令对象
	 * @param operatorName 操作人
	 */
	void updatePipeline(UpdatePipelineCommand command, String operatorName);

	/**
	 * 新增管道
	 *
	 * @param command 创建管道命令对象
	 */
	void insertPipeline(CreatePipelineCommand command);

	/**
	 * 判断管道是否存在
	 *
	 * @param pipelineId 管道ID
	 * @return 是否存在
	 */
	boolean isPipelineExist(Long pipelineId);

}
