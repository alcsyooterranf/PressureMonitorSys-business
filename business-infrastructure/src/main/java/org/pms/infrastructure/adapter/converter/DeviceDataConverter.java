package org.pms.infrastructure.adapter.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.pms.api.dto.resp.DeviceDataQueryView;
import org.pms.domain.devicedata.model.entity.DeviceDataEntity;
import org.pms.infrastructure.mapper.po.DeviceDataPO;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class DeviceDataConverter {

	@Mapping(target = "abnormalFlag", expression = "java(convertAbnormalFlagsToString(deviceDataEntity.getAbnormalFlags()))")
	public abstract DeviceDataPO entity2po(DeviceDataEntity deviceDataEntity);

	/**
	 * 将异常标志列表转换为字符串
	 * 例如：[OVER_PRESSURE, OVER_TEMPERATURE] -> "1,3"
	 *
	 * @param abnormalFlags 异常标志列表
	 * @return 逗号分隔的code字符串
	 */
	protected String convertAbnormalFlagsToString(java.util.List<org.pms.domain.devicedata.model.vo.AbnormalFlagVO> abnormalFlags) {
		if (abnormalFlags == null || abnormalFlags.isEmpty()) {
			return "0";
		}
		return org.pms.domain.devicedata.model.vo.AbnormalFlagVO.toCodes(abnormalFlags)
				.stream()
				.map(String::valueOf)
				.collect(java.util.stream.Collectors.joining(","));
	}
	
	@Mappings({
			@Mapping(target = "errorTime", source = "createTime"),
			@Mapping(target = "pressure", source = "pressure"),
			@Mapping(target = "temperature", source = "temperature"),
			@Mapping(target = "voltage", source = "voltage")
	})
	public abstract DeviceDataQueryView po2view(DeviceDataPO deviceDataPO);
	
	public abstract List<DeviceDataQueryView> pos2views(List<DeviceDataPO> deviceDataPOS);
	
}
