package org.pms.infrastructure.adapter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.pms.api.dto.resp.DeviceDataQueryView;
import org.pms.domain.devicedata.model.entity.DeviceDataEntity;
import org.pms.infrastructure.mapper.po.DeviceDataPO;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class DeviceDataConverter {

	@Mapping(target = "abnormalFlag", expression = "java(deviceDataEntity.getAbnormalFlagVO() != null ? deviceDataEntity.getAbnormalFlagVO().getCode().shortValue() : null)")
	public abstract DeviceDataPO entity2po(DeviceDataEntity deviceDataEntity);
	
	@Mappings({
			@Mapping(target = "errorTime", source = "createTime"),
			@Mapping(target = "pressure", source = "pressure"),
			@Mapping(target = "temperature", source = "temperature"),
			@Mapping(target = "voltage", source = "voltage")
	})
	public abstract DeviceDataQueryView po2view(DeviceDataPO deviceDataPO);
	
	public abstract List<DeviceDataQueryView> pos2views(List<DeviceDataPO> deviceDataPOS);
	
}
