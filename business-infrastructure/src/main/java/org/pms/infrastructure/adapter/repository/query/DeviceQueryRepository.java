package org.pms.infrastructure.adapter.repository.query;

import jakarta.annotation.Resource;
import org.pms.application.query.IDeviceQueryService;
import org.pms.api.dto.req.DeviceQueryCondition;
import org.pms.api.dto.resp.DeviceQueryView;
import org.pms.domain.terminal.model.entity.DeviceEntity;
import org.pms.infrastructure.adapter.converter.DeviceConverter;
import org.pms.infrastructure.mapper.IDeviceMapper;
import org.pms.infrastructure.mapper.IPipelineMapper;
import org.pms.infrastructure.mapper.po.DevicePO;
import org.pms.infrastructure.mapper.po.PipelinePO;
import org.pms.api.common.PageResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 设备查询仓储服务
 * @create 2025/12/10
 */
@Repository
public class DeviceQueryRepository implements IDeviceQueryService {
	
	@Resource
	private DeviceConverter deviceConverter;
	@Resource
	private IDeviceMapper deviceMapper;
	@Resource
	private IPipelineMapper pipelineMapper;
	
	@Override
	public PageResponse<DeviceQueryView> queryDevicePage(DeviceQueryCondition queryCondition) {
		// 分页查询
		Long count = deviceMapper.queryDeviceCount(queryCondition);
		List<DevicePO> devicePOS = deviceMapper.queryDeviceList(queryCondition);
		
		// 空值提前返回
		if (Objects.equals(0L, count) || devicePOS.isEmpty()) {
			return PageResponse.<DeviceQueryView>builder().count(count).build();
		}
		
		// 对象转换
		List<DeviceQueryView> deviceQueryViews = deviceConverter.pos2views(devicePOS);
		
		// 查询管道表
		List<Long> pipelineIds = deviceQueryViews.stream().map(DeviceQueryView::getPipelineId).toList();
		List<PipelinePO> pipelinePOS = pipelineMapper.queryByIdList(pipelineIds);
		
		// 补充前端返回对象的字段
		deviceQueryViews.forEach(device -> {
			PipelinePO pipelinePO = pipelinePOS.stream().filter(item -> item.getId().equals(device.getPipelineId())).findFirst().orElseGet(PipelinePO::new);
			device.setLocation(pipelinePO.getLocation());
			device.setPipelineName(pipelinePO.getPipelineName());
		});
		
		return PageResponse.<DeviceQueryView>builder().count(count).data(deviceQueryViews).build();
	}
	
	@Override
	public List<DeviceEntity> queryByIdList(List<Long> ids) {
		return deviceConverter.pos2entities(deviceMapper.queryByIdList(ids));
	}
	
}
