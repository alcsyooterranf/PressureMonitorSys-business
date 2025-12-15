package org.pms.domain.terminal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pms.api.dto.req.DeviceInsertReq;
import org.pms.api.dto.req.DeviceUpdateReq;

public interface IDeviceService {
	
	/**
	 * 逻辑删除设备
	 *
	 * @param id 设备id
	 */
	void deleteDeviceById(Long id, String securityContextEncoded) throws JsonProcessingException;
	
	/**
	 * 根据设备ID更新设备信息
	 *
	 * @param deviceUpdateReq 设备更新请求
	 */
	void updateDevice(DeviceUpdateReq deviceUpdateReq, String securityContextEncoded) throws JsonProcessingException;
	
	/**
	 * 解绑设备
	 *
	 * @param id 设备id
	 */
	void unbindDeviceById(Long id, String securityContextEncoded) throws JsonProcessingException;
	
	/**
	 * 新增设备
	 *
	 * @param deviceInsertReq 设备新增请求
	 */
	void addDevice(DeviceInsertReq deviceInsertReq);
	
}
