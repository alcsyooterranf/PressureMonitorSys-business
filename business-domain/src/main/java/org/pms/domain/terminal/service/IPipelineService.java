package org.pms.domain.terminal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pms.domain.terminal.model.command.CreatePipelineCommand;
import org.pms.domain.terminal.model.command.UpdatePipelineCommand;

/**
 * 管道服务接口
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author refactor
 * @date 2025-12-18
 */
public interface IPipelineService {

	/**
	 * 逻辑删除管道
	 *
	 * @param id 管道id
	 * @param securityContextEncoded 安全上下文
	 */
	void deletePipelineById(Long id, String securityContextEncoded) throws JsonProcessingException;

	/**
	 * 根据管道ID更新管道信息
	 *
	 * @param command 更新管道命令对象
	 * @param securityContextEncoded 安全上下文
	 */
	void updatePipeline(UpdatePipelineCommand command, String securityContextEncoded) throws JsonProcessingException;

	/**
	 * 新增管道
	 *
	 * @param command 创建管道命令对象
	 */
	void addPipeline(CreatePipelineCommand command);

}
