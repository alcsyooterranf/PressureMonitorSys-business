package org.pms.domain.devicedata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pms.domain.devicedata.model.entity.DeviceDataEntity;

public interface IDeviceDataService {
	
	/**
	 * 更新数据上报状态
	 *
	 * @param id 数据上报id
	 * @return 更改的行数
	 */
	int updateStatusById(Long id, String securityContextEncoded) throws JsonProcessingException;
	
	/**
	 * 逻辑删除数据上报
	 *
	 * @param id 数据上报id
	 * @return 更改的行数
	 */
	int deleteDeviceDataById(Long id, String securityContextEncoded) throws JsonProcessingException;
	
	/**
	 * 插入监控器数据上报记录
	 *
	 * @param deviceDataEntity 数据上报记录
	 */
	void addDeviceData(DeviceDataEntity deviceDataEntity);
	
}
