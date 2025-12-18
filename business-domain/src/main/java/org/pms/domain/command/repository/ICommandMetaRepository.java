package org.pms.domain.command.repository;

import org.pms.domain.command.model.command.CreateCommandMetaCommand;
import org.pms.domain.command.model.command.UpdateCommandMetaCommand;

/**
 * 指令元数据仓储接口
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author alcsyooterranf
 * @author refactor
 * @date 2025/12/15
 */
public interface ICommandMetaRepository {

	/**
	 * 创建指令元数据
	 * 如果已存在同pipeline_id + service_identifier的记录，则执行更新操作并使version + 1
	 *
	 * @param command 创建指令元数据命令对象
	 */
	void createCommandMeta(CreateCommandMetaCommand command);

	/**
	 * 更新指令元数据
	 *
	 * @param command 更新指令元数据命令对象
	 */
	void updateCommandMeta(UpdateCommandMetaCommand command);

	/**
	 * 废弃指令元数据（将status改为3-DEPRECATED）
	 *
	 * @param id 主键ID
	 */
	void deprecateCommandMeta(Long id);

	/**
	 * 验证指令元数据（将status从1-UNVERIFIED变为2-VERIFIED）
	 *
	 * @param id 主键ID
	 */
	void verifyCommandMeta(Long id);

	/**
	 * 根据pipelineId和serviceIdentifier获取payload_schema
	 *
	 * @param pipelineId        管道ID
	 * @param serviceIdentifier 服务标识符
	 * @return payload_schema JSON字符串，不存在返回null
	 */
	String getPayloadSchemaByPipelineAndService(Long pipelineId, String serviceIdentifier);

}

