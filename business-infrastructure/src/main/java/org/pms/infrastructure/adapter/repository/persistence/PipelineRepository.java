package org.pms.infrastructure.adapter.repository.persistence;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.terminal.model.command.CreatePipelineCommand;
import org.pms.domain.terminal.model.command.UpdatePipelineCommand;
import org.pms.domain.terminal.repository.IPipelineRepository;
import org.pms.infrastructure.adapter.converter.PipelineConverter;
import org.pms.infrastructure.mapper.IDeviceMapper;
import org.pms.infrastructure.mapper.IPipelineMapper;
import org.pms.infrastructure.mapper.po.PipelinePO;
import org.pms.types.BizCode;
import org.pms.types.BizException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * 管道仓储实现
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author refactor
 * @date 2025-12-18
 */
@Slf4j
@Repository
public class PipelineRepository implements IPipelineRepository {
	
	@Resource
	private PipelineConverter pipelineConverter;
	@Resource
	private IPipelineMapper pipelineMapper;
	@Resource
	private IDeviceMapper deviceMapper;
	@Resource
	private TransactionTemplate transactionTemplate;
	
	@Override
	public void deletePipelineById(Long id, String operatorName) {
		transactionTemplate.execute(status -> {
			// 1. 先删管道
			int deleteCnt = pipelineMapper.deletePipelineById(operatorName, id);
			if (1 != deleteCnt) {
				status.setRollbackOnly();
				throw new BizException(BizCode.PIPELINE_ID_ERROR.getCode(), BizCode.PIPELINE_ID_ERROR.getMessage());
			}
			// 2. 再解绑管道下的所有设备
			deleteCnt = deviceMapper.unbindDeviceByPipelineId(operatorName, id);
			if (0 == deleteCnt) {
				// 不回滚, 允许产品存在, 但产品下属的设备不存在
				// TODO: 日志记录, 便于后续问题排查
				log.warn("产品删除成功, 设备删除失败, 产品所属设备不存在或已删除");
			} else {
				log.info("产品和设备删除成功, 设备删除数量为: {}", deleteCnt);
			}
			return 1;
		});
	}
	
	@Override
	public void updatePipeline(UpdatePipelineCommand command, String operatorName) {
		transactionTemplate.execute(status -> {
			// 1.更新pipeline表中的字段
			int updateCnt = pipelineMapper.updatePipeline(operatorName, command);
			if (1 != updateCnt) {
				// 未更新成功则回滚
				status.setRollbackOnly();
				throw new BizException(BizCode.PIPELINE_ID_ERROR.getCode(), BizCode.PIPELINE_ID_ERROR.getMessage());
			}
			// 2.更新device表中的longitude和latitude字段
			updateCnt = deviceMapper.updateLngAndLatByPipelineId(operatorName, command.getId(),
					command.getLongitude(), command.getLatitude());
			if (0 == updateCnt) {
				// 不回滚, 允许产品存在, 但产品下属的设备不存在
				// TODO: 日志记录, 便于后续问题排查
				log.warn("产品更新成功, 设备更新失败, 产品所属设备不存在或已删除");
			} else {
				log.info("产品和设备更新成功, 设备更新数量为: {}", updateCnt);
			}
			return 1;
		});
	}
	
	@Override
	public void insertPipeline(CreatePipelineCommand command) {
		PipelinePO pipelinePO = pipelineConverter.command2PO(command);
		pipelineMapper.insertPipeline(pipelinePO);
	}
	
	@Override
	public boolean isPipelineExist(Long pipelineId) {
		List<Long> pipelineIds = pipelineMapper.queryIdsList();
		return pipelineIds.contains(pipelineId);
	}
	
}
