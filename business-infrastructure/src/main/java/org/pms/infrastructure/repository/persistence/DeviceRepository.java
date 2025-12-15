package org.pms.infrastructure.repository.persistence;

import com.pms.types.AppException;
import com.pms.types.ResponseCode;
import jakarta.annotation.Resource;
import org.pms.api.dto.req.DeviceInsertReq;
import org.pms.api.dto.req.DeviceUpdateReq;
import org.pms.domain.terminal.repository.IDeviceRepository;
import org.pms.infrastructure.adapter.DeviceConverter;
import org.pms.infrastructure.mapper.IDeviceMapper;
import org.pms.infrastructure.mapper.IPipelineMapper;
import org.pms.infrastructure.mapper.po.DevicePO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

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
			throw new AppException(ResponseCode.DEVICE_ID_ERROR.getCode(), ResponseCode.DEVICE_ID_ERROR.getMessage());
		}
	}
	
	@Override
	public void updateDevice(DeviceUpdateReq deviceUpdateReq, String operatorName) {
		transactionTemplate.execute(status -> {
			int updateCnt = deviceMapper.updateDevice(operatorName, deviceUpdateReq);
			if (1 != updateCnt) {
				// 未更新成功则回滚
				status.setRollbackOnly();
				throw new AppException(ResponseCode.DEVICE_ID_ERROR.getCode(),
						ResponseCode.DEVICE_ID_ERROR.getMessage());
			}
			// 同步更新安装地址location
			updateCnt = pipelineMapper.updateLocationById(operatorName, deviceUpdateReq.getProductId(),
					deviceUpdateReq.getLocation());
			if (1 != updateCnt) {
				// 未更新成功则回滚
				status.setRollbackOnly();
				throw new AppException(ResponseCode.PRODUCT_ID_ERROR.getCode(),
						ResponseCode.PRODUCT_ID_ERROR.getMessage());
			}
			return 1;
		});
	}
	
	@Override
	public void unbindDeviceById(Long id, String operatorName) {
		int updateCnt = deviceMapper.unbindDeviceById(operatorName, id);
		if (1 != updateCnt) {
			throw new AppException(ResponseCode.DEVICE_ID_ERROR.getCode(), ResponseCode.DEVICE_ID_ERROR.getMessage());
		}
	}
	
	@Override
	public void insertDevice(DeviceInsertReq deviceInsertReq) {
		DevicePO devicePO = deviceConverter.insertReq2PO(deviceInsertReq);
		List<Long> ids = pipelineMapper.queryIdsList();
		if (!ids.contains(devicePO.getPipelineId())) {
			throw new AppException(ResponseCode.PRODUCT_ID_ERROR.getCode(), ResponseCode.PRODUCT_ID_ERROR.getMessage());
		}
		deviceMapper.insertDevice(devicePO);
	}
	
}
