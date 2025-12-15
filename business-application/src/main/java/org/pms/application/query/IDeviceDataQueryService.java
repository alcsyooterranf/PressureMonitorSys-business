package org.pms.application.query;

import org.pms.api.dto.req.DeviceDataQueryCondition;
import org.pms.api.dto.resp.DeviceDataQueryView;
import org.pms.api.common.PageResponse;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 设备数据查询仓储接口
 * @create 2025/12/10
 */
public interface IDeviceDataQueryService {
	
	/**
	 * 按条件查询所有数据, 支持的条件有：设备编码、处理状态、开始时间、结束时间
	 *
	 * @param queryCondition 查询条件
	 * @return 设备数据视图对象
	 */
	PageResponse<DeviceDataQueryView> queryDeviceDataPage(DeviceDataQueryCondition queryCondition);
	
}
