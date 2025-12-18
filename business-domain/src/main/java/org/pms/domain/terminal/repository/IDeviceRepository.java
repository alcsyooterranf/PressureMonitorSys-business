package org.pms.domain.terminal.repository;

import org.pms.domain.terminal.model.command.CreateDeviceCommand;
import org.pms.domain.terminal.model.command.UpdateDeviceCommand;
import org.pms.domain.terminal.model.entity.DeviceEntity;

/**
 * 设备仓储接口
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author refactor
 * @date 2025-12-18
 */
public interface IDeviceRepository {

	/**
	 * 逻辑删除设备
	 *
	 * @param id 设备id
	 * @param operatorName 操作人
	 */
	void deleteDeviceById(Long id, String operatorName);

	/**
	 * 根据设备ID更新设备信息
	 *
	 * @param command 更新设备命令对象
	 * @param operatorName 操作人
	 */
	void updateDevice(UpdateDeviceCommand command, String operatorName);

	/**
	 * 解绑设备
	 *
	 * @param id 设备id
	 * @param operatorName 操作人
	 */
	void unbindDeviceById(Long id, String operatorName);

	/**
	 * 新增设备
	 *
	 * @param command 创建设备命令对象
	 */
	void insertDevice(CreateDeviceCommand command);
	
	/**
	 * 根据设备SN查询设备信息
	 *
	 * @param deviceSN 设备SN
	 * @return 设备信息
	 */
	DeviceEntity queryParameterLimitsBySN(String deviceSN);
}
