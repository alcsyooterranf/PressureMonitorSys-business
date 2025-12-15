package org.pms.domain.terminal.repository;

import org.pms.api.dto.req.DeviceInsertReq;
import org.pms.api.dto.req.DeviceUpdateReq;

public interface IDeviceRepository {
	
	/**
	 * 逻辑删除设备
	 *
	 * @param id 设备id
	 */
	void deleteDeviceById(Long id, String operatorName);
	
	/**
	 * 根据设备ID更新设备信息
	 *
	 * @param deviceUpdateReq 设备更新请求
	 */
	void updateDevice(DeviceUpdateReq deviceUpdateReq, String operatorName);
	
	/**
	 * 解绑设备
	 *
	 * @param id 设备id
	 */
	void unbindDeviceById(Long id, String operatorName);
	
	/**
	 * 新增设备
	 *
	 * @param deviceInsertReq 设备新增请求
	 */
	void insertDevice(DeviceInsertReq deviceInsertReq);
	
}
