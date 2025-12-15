package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.pms.api.dto.req.DeviceDataQueryCondition;
import org.pms.infrastructure.mapper.po.DeviceDataPO;

import java.util.List;

@Mapper
public interface IDeviceDataMapper {
	
	/**
	 * 插入设备数据
	 *
	 * @param deviceDataPO 设备数据
	 */
	void insertDeviceData(DeviceDataPO deviceDataPO);
	
	/**
	 * 删除设备数据
	 *
	 * @param operatorName 操作人
	 * @param id           数据ID
	 * @return 删除的行数
	 */
	int deleteDataReportById(String operatorName, Long id);
	
	/**
	 * 查询设备分页数据数量
	 *
	 * @param deviceDataQueryCondition 查询条件
	 * @return 设备数据数量
	 */
	Long queryDeviceDataCount(DeviceDataQueryCondition deviceDataQueryCondition);
	
	/**
	 * 查询设备分页数据列表
	 *
	 * @param deviceDataQueryCondition 查询条件
	 * @return 设备数据列表
	 */
	List<DeviceDataPO> queryDeviceDataList(DeviceDataQueryCondition deviceDataQueryCondition);
	
	/**
	 * 更新设备数据状态
	 *
	 * @param operatorName 操作人
	 * @param id           数据ID
	 * @return 更新的行数
	 */
	int updateStateById(String operatorName, Long id);
	
}
