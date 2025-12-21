package org.pms.infrastructure.adapter.repository.persistence;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.command.model.command.CreateCommandMetaCommand;
import org.pms.domain.command.model.command.UpdateCommandMetaCommand;
import org.pms.domain.command.model.valobj.StatusVO;
import org.pms.domain.command.repository.ICommandMetaRepository;
import org.pms.infrastructure.adapter.converter.CommandMetaConverter;
import org.pms.infrastructure.mapper.ICommandMetaMapper;
import org.pms.infrastructure.mapper.po.CommandMetaPO;
import org.pms.types.BizCode;
import org.pms.types.BizException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 指令元数据仓储实现
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author alcsyooterranf
 * @author refactor
 * @date 2025/12/15
 */
@Slf4j
@Repository
public class CommandMetaRepository implements ICommandMetaRepository {

	@Resource
	private CommandMetaConverter commandMetaConverter;
	@Resource
	private ICommandMetaMapper commandMetaMapper;
	@Resource
	private TransactionTemplate transactionTemplate;

	@Override
	public void createCommandMeta(CreateCommandMetaCommand command) {
		transactionTemplate.execute(status -> {
			try {
				// 检查是否已存在同pipeline_id + service_identifier的记录
				CommandMetaPO existingPO = commandMetaMapper.selectByPipelineIdAndServiceIdentifier(
						command.getPipelineId(),
						command.getServiceIdentifier()
				);

				if (existingPO != null) {
					// 已存在，执行更新操作并使version + 1
					CommandMetaPO updatePO = CommandMetaPO.builder()
							.pipelineId(command.getPipelineId())
							.serviceIdentifier(command.getServiceIdentifier())
							.name(command.getName())
							.payloadSchema(command.getPayloadSchema())
							.remark(command.getRemark())
							.build();

					int updateCnt = commandMetaMapper.updateByPipelineIdAndServiceIdentifier(updatePO);
					if (updateCnt != 1) {
						status.setRollbackOnly();
						throw new BizException(BizCode.COMMAND_META_UPDATE_FAILED);
					}
					log.info("指令元数据已存在，执行更新操作: pipelineId={}, serviceIdentifier={}, version={}",
							command.getPipelineId(), command.getServiceIdentifier(), existingPO.getVersion() + 1);
				} else {
					// 不存在，执行插入操作
					CommandMetaPO insertPO = commandMetaConverter.command2po(command);
					insertPO.setVersion(1);
					insertPO.setStatus(StatusVO.UNVERIFIED.getCode());

					int insertCnt = commandMetaMapper.insert(insertPO);
					if (insertCnt != 1) {
						status.setRollbackOnly();
						throw new BizException(BizCode.COMMAND_META_CREATE_FAILED);
					}
					log.info("指令元数据创建成功: pipelineId={}, serviceIdentifier={}",
							command.getPipelineId(), command.getServiceIdentifier());
				}

				return 1;
			} catch (BizException e) {
				status.setRollbackOnly();
				throw e;
			} catch (Exception e) {
				status.setRollbackOnly();
				log.error("创建指令元数据异常", e);
				throw new BizException("ERR_BIZ_COMMAND_META_1004", "创建指令元数据异常: " + e.getMessage());
			}
		});
	}
	
	@Override
	public void updateCommandMeta(UpdateCommandMetaCommand command) {
		transactionTemplate.execute(status -> {
			try {
				// 执行更新
				CommandMetaPO updatePO = commandMetaConverter.command2po(command);
				int updateCnt = commandMetaMapper.updateById(updatePO);
				if (updateCnt != 1) {
					status.setRollbackOnly();
					throw new BizException(BizCode.COMMAND_META_UPDATE_ID_NOT_EXIST);
				}

				log.info("指令元数据更新成功: id={}", command.getId());
				return 1;
			} catch (BizException e) {
				status.setRollbackOnly();
				throw e;
			} catch (Exception e) {
				status.setRollbackOnly();
				log.error("更新指令元数据异常", e);
				throw new BizException(BizCode.COMMAND_META_UPDATE_EXCEPTION, "更新指令元数据异常: " + e.getMessage());
			}
		});
	}
	
	
	@Override
	public void deprecateCommandMeta(Long id) {
		int updateCnt = commandMetaMapper.updateStatusById(id, StatusVO.DEPRECATED.getCode());
		if (updateCnt != 1) {
			throw new BizException("ERR_BIZ_COMMAND_META_1007", "废弃指令元数据失败，ID不存在");
		}
		log.info("指令元数据已废弃: id={}", id);
	}

	@Override
	public void verifyCommandMeta(Long id) {
		// 先查询当前状态
		CommandMetaPO commandMetaPO = commandMetaMapper.selectById(id);
		if (commandMetaPO == null) {
			throw new BizException(BizCode.COMMAND_META_VERIFY_ID_NOT_EXIST);
		}

		// 只有UNVERIFIED状态才能变为VERIFIED
		if (!StatusVO.UNVERIFIED.getCode().equals(commandMetaPO.getStatus())) {
			throw new BizException(BizCode.COMMAND_META_VERIFY_STATUS_ERROR);
		}

		int updateCnt = commandMetaMapper.updateStatusById(id, StatusVO.VERIFIED.getCode());
		if (updateCnt != 1) {
			throw new BizException(BizCode.COMMAND_META_VERIFY_FAILED);
		}
		log.info("指令元数据已验证: id={}", id);
	}
	
	@Override
	public String getPayloadSchemaByPipelineAndService(Long pipelineId, String serviceIdentifier) {
		CommandMetaPO commandMetaPO = commandMetaMapper.selectByPipelineIdAndServiceIdentifier(pipelineId, serviceIdentifier);
		return commandMetaPO == null ? null : commandMetaPO.getPayloadSchema();
	}
	
}