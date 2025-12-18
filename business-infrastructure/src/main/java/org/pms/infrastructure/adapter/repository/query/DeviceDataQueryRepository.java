package org.pms.infrastructure.adapter.repository.query;

import jakarta.annotation.Resource;
import org.pms.application.query.IDeviceDataQueryService;
import org.pms.api.dto.req.DeviceDataQueryCondition;
import org.pms.api.dto.resp.DeviceDataQueryView;
import org.pms.infrastructure.adapter.converter.DeviceDataConverter;
import org.pms.infrastructure.mapper.IDeviceDataMapper;
import org.pms.infrastructure.mapper.IPipelineMapper;
import org.pms.infrastructure.mapper.po.DeviceDataPO;
import org.pms.infrastructure.mapper.po.PipelinePO;
import org.pms.api.common.PageResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 设备数据查询仓储实现类
 * @create 2025/12/10
 */
@Repository
public class DeviceDataQueryRepository implements IDeviceDataQueryService {
	
	@Resource
	private DeviceDataConverter deviceDataConverter;
	@Resource
	private IDeviceDataMapper deviceDataMapper;
	@Resource
	private IPipelineMapper pipelineMapper;
	
	@Override
	public PageResponse<DeviceDataQueryView> queryDeviceDataPage(DeviceDataQueryCondition queryCondition) {
		// 分页查询
		Long count = deviceDataMapper.queryDeviceDataCount(queryCondition);
		List<DeviceDataPO> deviceDataPOS = deviceDataMapper.queryDeviceDataList(queryCondition);
		
		// 空值提前返回
		if (Objects.equals(0L, count) || deviceDataPOS.isEmpty()) {
			return PageResponse.<DeviceDataQueryView>builder().count(count).build();
		}
		
		// 对象转换
		List<DeviceDataQueryView> deviceDataQueryViews = deviceDataConverter.pos2views(deviceDataPOS);
		
		// 查询管道表
		List<Long> pipelineIds = deviceDataQueryViews.stream().map(DeviceDataQueryView::getPipelineId).toList();
		List<PipelinePO> pipelinePOS = pipelineMapper.queryByIdList(pipelineIds);
		
		// 补充前端返回对象的字段
		deviceDataQueryViews.forEach(device -> {
			PipelinePO pipelinePO = pipelinePOS.stream().filter(item -> item.getId().equals(device.getPipelineId())).findFirst().orElseGet(PipelinePO::new);
			device.setLocation(pipelinePO.getLocation());
		});
		
		return PageResponse.<DeviceDataQueryView>builder().count(count).data(deviceDataQueryViews).build();
	}
	
}
