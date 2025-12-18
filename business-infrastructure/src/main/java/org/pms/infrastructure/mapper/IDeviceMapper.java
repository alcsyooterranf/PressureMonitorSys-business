package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.pms.api.dto.req.DeviceQueryCondition;
import org.pms.domain.terminal.model.command.UpdateDeviceCommand;
import org.pms.infrastructure.mapper.po.DevicePO;

import java.util.List;

/**
 * 设备Mapper
 * <p>
 * 重构说明：
 * - updateDevice方法参数从DeviceUpdateReq改为UpdateDeviceCommand
 * - 解除对API层DTO的依赖
 *
 * @author refactor
 * @date 2025-12-18
 */
@Mapper
public interface IDeviceMapper {

	Long queryDeviceCount(DeviceQueryCondition queryCondition);

	List<DevicePO> queryDeviceList(DeviceQueryCondition queryCondition);

	int deleteDeviceById(String operatorName, Long id);

	int updateDevice(String operatorName, UpdateDeviceCommand command);
	
	List<DevicePO> queryByIdList(List<Long> ids);
	
	int updateLngAndLatByPipelineId(String operatorName, Long pipelineId,
									String longitude, String latitude);
	
	int unbindDeviceById(String operatorName, Long id);
	
	int unbindDeviceByPipelineId(String operatorName, Long pipelineId);
	
	DevicePO queryParameterLimitsBySN(String deviceSN);
	
	void insertDevice(DevicePO devicePO);
	
	DevicePO checkDeviceSNAndPipelineSN(Long deviceSN, Long pipelineSN);
	
}
