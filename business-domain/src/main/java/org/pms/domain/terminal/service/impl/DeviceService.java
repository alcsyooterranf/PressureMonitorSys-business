package org.pms.domain.terminal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.rbac.model.req.SecurityContextHeader;
import org.pms.api.dto.req.DeviceInsertReq;
import org.pms.api.dto.req.DeviceUpdateReq;
import org.pms.domain.terminal.repository.IDeviceRepository;
import org.pms.domain.terminal.service.IDeviceService;
import org.springframework.stereotype.Service;

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
	public void updateDevice(DeviceUpdateReq deviceUpdateReq, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		deviceRepository.updateDevice(deviceUpdateReq, securityContext.getUsername());
	}
	
	@Override
	public void unbindDeviceById(Long id, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		deviceRepository.unbindDeviceById(id, securityContext.getUsername());
	}
	
	@Override
	public void addDevice(DeviceInsertReq deviceInsertReq) {
		deviceRepository.insertDevice(deviceInsertReq);
	}
	
}
