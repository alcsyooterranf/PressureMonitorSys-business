package org.pms.infrastructure.repository.persistence;

import com.pms.types.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.dto.req.CommandMetaInsertReq;
import org.pms.api.dto.req.CommandMetaUpdateReq;
import org.pms.domain.command.model.valobj.StatusVO;
import org.pms.domain.command.repository.ICommandMetaRepository;
import org.pms.infrastructure.adapter.CommandMetaConverter;
import org.pms.infrastructure.mapper.ICommandMetaMapper;
import org.pms.infrastructure.mapper.po.CommandMetaPO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据仓储实现
 * @create 2025/12/15
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
	public void createCommandMeta(CommandMetaInsertReq commandMetaInsertReq) {
		transactionTemplate.execute(status -> {
			try {
				// 检查是否已存在同pipeline_id + service_identifier的记录
				CommandMetaPO existingPO = commandMetaMapper.selectByPipelineIdAndServiceIdentifier(
						commandMetaInsertReq.getPipelineId(),
						commandMetaInsertReq.getServiceIdentifier()
				);
				
				if (existingPO != null) {
					// 已存在，执行更新操作并使version + 1
					CommandMetaPO updatePO = CommandMetaPO.builder()
							.pipelineId(commandMetaInsertReq.getPipelineId())
							.serviceIdentifier(commandMetaInsertReq.getServiceIdentifier())
							.name(commandMetaInsertReq.getName())
							.payloadSchema(commandMetaInsertReq.getPayloadSchema())
							.remark(commandMetaInsertReq.getRemark())
							.build();
					
					int updateCnt = commandMetaMapper.updateByPipelineIdAndServiceIdentifier(updatePO);
					if (updateCnt != 1) {
						status.setRollbackOnly();
						throw new AppException("ERR_BIZ_COMMAND_META_1002", "更新指令元数据失败");
					}
					log.info("指令元数据已存在，执行更新操作: pipelineId={}, serviceIdentifier={}, version={}",
							commandMetaInsertReq.getPipelineId(), commandMetaInsertReq.getServiceIdentifier(), existingPO.getVersion() + 1);
				} else {
					// 不存在，执行插入操作
					CommandMetaPO insertPO = commandMetaConverter.insertReq2po(commandMetaInsertReq);
					insertPO.setVersion(1);
					insertPO.setStatus(StatusVO.UNVERIFIED.getCode());
					
					int insertCnt = commandMetaMapper.insert(insertPO);
					if (insertCnt != 1) {
						status.setRollbackOnly();
						throw new AppException("ERR_BIZ_COMMAND_META_1003", "创建指令元数据失败");
					}
					log.info("指令元数据创建成功: pipelineId={}, serviceIdentifier={}",
							commandMetaInsertReq.getPipelineId(), commandMetaInsertReq.getServiceIdentifier());
				}
				
				return 1;
			} catch (AppException e) {
				status.setRollbackOnly();
				throw e;
			} catch (Exception e) {
				status.setRollbackOnly();
				log.error("创建指令元数据异常", e);
				throw new AppException("ERR_BIZ_COMMAND_META_1004", "创建指令元数据异常: " + e.getMessage());
			}
		});
	}
	
	@Override
	public void updateCommandMeta(CommandMetaUpdateReq commandMetaUpdateReq) {
		transactionTemplate.execute(status -> {
			try {
				// 执行更新
				CommandMetaPO updatePO = commandMetaConverter.updateReq2po(commandMetaUpdateReq);
				int updateCnt = commandMetaMapper.updateById(updatePO);
				if (updateCnt != 1) {
					status.setRollbackOnly();
					throw new AppException("ERR_BIZ_COMMAND_META_1005", "更新指令元数据失败，ID不存在");
				}
				
				log.info("指令元数据更新成功: id={}", commandMetaUpdateReq.getId());
				return 1;
			} catch (AppException e) {
				status.setRollbackOnly();
				throw e;
			} catch (Exception e) {
				status.setRollbackOnly();
				log.error("更新指令元数据异常", e);
				throw new AppException("ERR_BIZ_COMMAND_META_1006", "更新指令元数据异常: " + e.getMessage());
			}
		});
	}
	
	
	@Override
	public void deprecateCommandMeta(Long id) {
		int updateCnt = commandMetaMapper.updateStatusById(id, StatusVO.DEPRECATED.getCode());
		if (updateCnt != 1) {
			throw new AppException("ERR_BIZ_COMMAND_META_1007", "废弃指令元数据失败，ID不存在");
		}
		log.info("指令元数据已废弃: id={}", id);
	}
	
	@Override
	public void verifyCommandMeta(Long id) {
		// 先查询当前状态
		CommandMetaPO commandMetaPO = commandMetaMapper.selectById(id);
		if (commandMetaPO == null) {
			throw new AppException("ERR_BIZ_COMMAND_META_1008", "验证指令元数据失败，ID不存在");
		}
		
		// 只有UNVERIFIED状态才能变为VERIFIED
		if (!StatusVO.UNVERIFIED.getCode().equals(commandMetaPO.getStatus())) {
			throw new AppException("ERR_BIZ_COMMAND_META_1009", "只有未验证状态的指令元数据才能被验证");
		}
		
		int updateCnt = commandMetaMapper.updateStatusById(id, StatusVO.VERIFIED.getCode());
		if (updateCnt != 1) {
			throw new AppException("ERR_BIZ_COMMAND_META_1010", "验证指令元数据失败");
		}
		log.info("指令元数据已验证: id={}", id);
	}
	
	@Override
	public String getPayloadSchemaByPipelineAndService(Long pipelineId, String serviceIdentifier) {
		CommandMetaPO commandMetaPO = commandMetaMapper.selectByPipelineIdAndServiceIdentifier(pipelineId, serviceIdentifier);
		return commandMetaPO == null ? null : commandMetaPO.getPayloadSchema();
	}
	
}