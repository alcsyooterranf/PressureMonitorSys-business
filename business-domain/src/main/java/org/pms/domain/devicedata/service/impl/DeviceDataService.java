package org.pms.domain.devicedata.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.auth.model.req.SecurityContextHeader;
import org.pms.domain.devicedata.model.entity.DeviceDataEntity;
import org.pms.domain.devicedata.repository.IDeviceDataRepository;
import org.pms.domain.devicedata.service.IDeviceDataService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeviceDataService implements IDeviceDataService {
	
	@Resource
	private IDeviceDataRepository deviceDataRepository;
	
	@Override
	public int updateStatusById(Long id, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		return deviceDataRepository.updateStatusById(id, securityContext.getUsername());
	}
	
	@Override
	public int deleteDeviceDataById(Long id, String securityContextEncoded) throws JsonProcessingException {
		SecurityContextHeader securityContext = SecurityContextHeader.build(securityContextEncoded);
		return deviceDataRepository.deleteDeviceDataById(id, securityContext.getUsername());
	}
	
	@Override
	public void addDeviceData(DeviceDataEntity deviceDataEntity) {
		deviceDataRepository.addDeviceData(deviceDataEntity);
	}
	
}
