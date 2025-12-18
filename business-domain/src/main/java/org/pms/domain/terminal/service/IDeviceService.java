package org.pms.domain.terminal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pms.domain.terminal.model.command.CreateDeviceCommand;
import org.pms.domain.terminal.model.command.UpdateDeviceCommand;
import org.pms.domain.terminal.model.entity.DeviceEntity;

/**
 * 设备服务接口
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author refactor
 * @date 2025-12-18
 */
public interface IDeviceService {

	/**
	 * 逻辑删除设备
	 *
	 * @param id 设备id
	 * @param securityContextEncoded 安全上下文
	 */
	void deleteDeviceById(Long id, String securityContextEncoded) throws JsonProcessingException;

	/**
	 * 根据设备ID更新设备信息
	 *
	 * @param command 更新设备命令对象
	 * @param securityContextEncoded 安全上下文
	 */
	void updateDevice(UpdateDeviceCommand command, String securityContextEncoded) throws JsonProcessingException;

	/**
	 * 解绑设备
	 *
	 * @param id 设备id
	 * @param securityContextEncoded 安全上下文
	 */
	void unbindDeviceById(Long id, String securityContextEncoded) throws JsonProcessingException;

	/**
	 * 新增设备
	 *
	 * @param command 创建设备命令对象
	 */
	void addDevice(CreateDeviceCommand command);
	
	/**
	 * 根据设备SN查询设备信息
	 *
	 * @param deviceSN 设备SN
	 * @return 设备信息
	 */
	DeviceEntity queryParameterLimitsBySN(String deviceSN);
}
