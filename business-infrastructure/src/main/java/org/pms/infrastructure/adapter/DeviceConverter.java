package org.pms.infrastructure.adapter;

import org.mapstruct.Mapper;
import org.pms.api.dto.resp.DeviceQueryView;
import org.pms.domain.terminal.model.entity.DeviceEntity;
import org.pms.api.dto.req.DeviceInsertReq;
import org.pms.infrastructure.mapper.po.DevicePO;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class DeviceConverter {
	
	/**
	 * 单个JavaBean转换 DevicePO -> DeviceEntity
	 *
	 * @param devicePO PO
	 * @return DeviceEntity
	 */
	public abstract DeviceEntity po2entity(DevicePO devicePO);
	
	/**
	 * 批量转换 List<DevicePO> -> List<DeviceEntity>
	 *
	 * @param devicePOS PO列表
	 * @return List<DeviceEntity>
	 */
	public abstract List<DeviceEntity> pos2entities(List<DevicePO> devicePOS);
	
	/**
	 * 单个JavaBean转换 DevicePO -> DeviceQueryView
	 *
	 * @param devicePO PO
	 * @return DeviceQueryView
	 */
	public abstract DeviceQueryView po2view(DevicePO devicePO);
	
	/**
	 * 批量转换 List<DevicePO> -> List<DeviceQueryView>
	 *
	 * @param devicePOS PO列表
	 * @return List<DeviceQueryView>
	 */
	public abstract List<DeviceQueryView> pos2views(List<DevicePO> devicePOS);
	
	/**
	 * 单个JavaBean转换 DeviceInsertReq -> DevicePO
	 *
	 * @param deviceInsertReq insertReq
	 * @return DevicePO
	 */
	public abstract DevicePO insertReq2PO(DeviceInsertReq deviceInsertReq);
	
}
