package org.pms.application.query;

import org.pms.api.dto.req.DeviceQueryCondition;
import org.pms.api.dto.resp.DeviceQueryView;
import org.pms.domain.terminal.model.entity.DeviceEntity;
import org.pms.api.common.PageResponse;

import java.util.List;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 设备查询仓储接口
 * @create 2025/12/10
 */
public interface IDeviceQueryService {
	
	/**
	 * 按条件查询所有数据, 支持的条件有：协议编码、设备编码、客户名称、开始时间、结束时间
	 *
	 * @param queryCondition 查询条件
	 * @return 设备视图对象
	 */
	PageResponse<DeviceQueryView> queryDevicePage(DeviceQueryCondition queryCondition);
	
	/**
	 * 根据id列表查询设备信息
	 *
	 * @param ids 设备id列表
	 * @return 设备信息列表
	 */
	List<DeviceEntity> queryByIdList(List<Long> ids);
	
}
