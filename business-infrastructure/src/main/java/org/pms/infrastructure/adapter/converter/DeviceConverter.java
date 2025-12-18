package org.pms.infrastructure.adapter.converter;

import org.mapstruct.Mapper;
import org.pms.api.dto.resp.DeviceQueryView;
import org.pms.domain.terminal.model.command.CreateDeviceCommand;
import org.pms.domain.terminal.model.entity.DeviceEntity;
import org.pms.infrastructure.mapper.po.DevicePO;

import java.util.List;

/**
 * 设备转换器
 * <p>
 * 重构说明：
 * - 移除API DTO转换方法（insertReq2PO）
 * - 新增Command对象转换方法（command2PO）
 * - 只保留Entity↔PO和PO→View的转换
 *
 * @author refactor
 * @date 2025-12-18
 */
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
	 * 单个JavaBean转换 CreateDeviceCommand -> DevicePO
	 *
	 * @param command 创建设备命令对象
	 * @return DevicePO
	 */
	public abstract DevicePO command2PO(CreateDeviceCommand command);

}
