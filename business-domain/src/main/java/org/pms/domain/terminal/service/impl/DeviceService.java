package org.pms.domain.terminal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.rbac.model.req.SecurityContextHeader;
import org.pms.domain.terminal.model.command.CreateDeviceCommand;
import org.pms.domain.terminal.model.command.UpdateDeviceCommand;
import org.pms.domain.terminal.model.entity.DeviceEntity;
import org.pms.domain.terminal.repository.IDeviceRepository;
import org.pms.domain.terminal.service.IDeviceService;
import org.springframework.stereotype.Service;

/**
 * 设备服务实现
 * <p>
 * 重构说明：
 * - 使用Domain层的Command对象替代API层的DTO
 * - 解除对API层的依赖
 *
 * @author refactor
 * @date 2025-12-18
 */
@Slf4j
@Service
public class DeviceService implements IDeviceService {
	
	@Resource
	private IDeviceRepository deviceRepository;
	
	@Override
	public void deleteDeviceById(Long id, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		deviceRepository.deleteDeviceById(id, securityContext.getUsername());
	}
	
	@Override
	public void updateDevice(UpdateDeviceCommand command, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		deviceRepository.updateDevice(command, securityContext.getUsername());
	}
	
	@Override
	public void unbindDeviceById(Long id, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		deviceRepository.unbindDeviceById(id, securityContext.getUsername());
	}
	
	@Override
	public void addDevice(CreateDeviceCommand command) {
		deviceRepository.insertDevice(command);
	}
	
	@Override
	public DeviceEntity queryParameterLimitsBySN(String deviceSN) {
		return deviceRepository.queryParameterLimitsBySN(deviceSN);
	}
	
}
