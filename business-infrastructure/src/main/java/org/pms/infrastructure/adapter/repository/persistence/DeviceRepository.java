package org.pms.infrastructure.adapter.repository.persistence;

import jakarta.annotation.Resource;
import org.pms.domain.terminal.model.command.CreateDeviceCommand;
import org.pms.domain.terminal.model.command.UpdateDeviceCommand;
import org.pms.domain.terminal.model.entity.DeviceEntity;
import org.pms.domain.terminal.repository.IDeviceRepository;
import org.pms.infrastructure.adapter.converter.DeviceConverter;
import org.pms.infrastructure.mapper.IDeviceMapper;
import org.pms.infrastructure.mapper.IPipelineMapper;
import org.pms.infrastructure.mapper.po.DevicePO;
import org.pms.types.BizCode;
import org.pms.types.exception.BizException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * 设备仓储实现
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author refactor
 * @date 2025-12-18
 */
@Repository
public class DeviceRepository implements IDeviceRepository {
	
	@Resource
	private DeviceConverter deviceConverter;
	@Resource
	private IDeviceMapper deviceMapper;
	@Resource
	private IPipelineMapper pipelineMapper;
	@Resource
	private TransactionTemplate transactionTemplate;
	
	@Override
	public void deleteDeviceById(Long id, String operatorName) {
		int deleteCnt = deviceMapper.deleteDeviceById(operatorName, id);
		if (1 != deleteCnt) {
			throw new BizException(BizCode.DEVICE_ID_ERROR.getCode(), BizCode.DEVICE_ID_ERROR.getMessage());
		}
	}
	
	@Override
	public void updateDevice(UpdateDeviceCommand command, String operatorName) {
		transactionTemplate.execute(status -> {
			int updateCnt = deviceMapper.updateDevice(operatorName, command);
			if (1 != updateCnt) {
				// 未更新成功则回滚
				status.setRollbackOnly();
				throw new BizException(BizCode.DEVICE_ID_ERROR.getCode(), BizCode.DEVICE_ID_ERROR.getMessage());
			}
			// 同步更新安装地址location
			updateCnt = pipelineMapper.updateLocationById(operatorName, command.getPipelineId(),
					command.getLocation());
			if (1 != updateCnt) {
				// 未更新成功则回滚
				status.setRollbackOnly();
				throw new BizException(BizCode.PRODUCT_ID_ERROR.getCode(), BizCode.PRODUCT_ID_ERROR.getMessage());
			}
			return 1;
		});
	}
	
	@Override
	public void unbindDeviceById(Long id, String operatorName) {
		int updateCnt = deviceMapper.unbindDeviceById(operatorName, id);
		if (1 != updateCnt) {
			throw new BizException(BizCode.DEVICE_ID_ERROR.getCode(), BizCode.DEVICE_ID_ERROR.getMessage());
		}
	}
	
	@Override
	public void insertDevice(CreateDeviceCommand command) {
		DevicePO devicePO = deviceConverter.command2PO(command);
		List<Long> ids = pipelineMapper.queryIdsList();
		if (!ids.contains(devicePO.getPipelineId())) {
			throw new BizException(BizCode.PRODUCT_ID_ERROR.getCode(), BizCode.PRODUCT_ID_ERROR.getMessage());
		}
		deviceMapper.insertDevice(devicePO);
	}
	
	@Override
	public DeviceEntity queryParameterLimitsBySN(String deviceSN) {
		DevicePO devicePO = deviceMapper.queryParameterLimitsBySN(deviceSN);
		return deviceConverter.po2entity(devicePO);
	}
	
}
