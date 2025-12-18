package org.pms.infrastructure.adapter.repository.persistence;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.domain.devicedata.model.entity.DeviceDataEntity;
import org.pms.domain.devicedata.model.vo.AbnormalFlagVO;
import org.pms.domain.devicedata.repository.IDeviceDataRepository;
import org.pms.infrastructure.adapter.converter.DeviceDataConverter;
import org.pms.infrastructure.adapter.repository.clickhouse.ClickHouseDataBuffer;
import org.pms.infrastructure.mapper.IDeviceDataMapper;
import org.pms.infrastructure.mapper.IDeviceMapper;
import org.pms.infrastructure.mapper.po.DeviceDataPO;
import org.pms.infrastructure.mapper.po.DevicePO;
import org.pms.types.BizCode;
import org.pms.types.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 数据上报仓储实现类
 * 职责:
 * 1. 保存监控数据到MySQL(用于业务查询)
 * 2. 异步保存监控数据到ClickHouse(用于大数据分析)
 * 3. 触发告警服务(微信推送 + WebSocket推送)
 *
 * @author alcsyooterranf
 * @since 2024-11-24
 */
@Slf4j
@Repository
public class DeviceDataRepository implements IDeviceDataRepository {
	
	@Resource
	private DeviceDataConverter deviceDataConverter;
	@Resource
	private IDeviceDataMapper deviceDataMapper;
	@Resource
	private IDeviceMapper deviceMapper;
	
	/**
	 * ClickHouse数据缓冲区
	 * 用于异步批量写入ClickHouse*
	 * 为什么使用@Autowired而不是构造器注入?
	 * - ClickHouse是可选的,如果没有配置ClickHouse,这个Bean可能不存在
	 * - 使用@Autowired(required=false)可以避免启动失败
	 */
	@Autowired(required = false)
	private ClickHouseDataBuffer clickHouseDataBuffer;
	
	@Override
	public int updateStatusById(Long id, String operatorName) {
		int updateAbnormalReportCount = deviceDataMapper.updateStateById(operatorName, id);
		if (1 != updateAbnormalReportCount) {
			throw new BizException(BizCode.ABNORMAL_REPORT_ID_ERROR.getCode(), BizCode.ABNORMAL_REPORT_ID_ERROR.getMessage());
		}
		return updateAbnormalReportCount;
	}
	
	@Override
	public int deleteDeviceDataById(Long id, String operatorName) {
		int deleteCnt = deviceDataMapper.deleteDataReportById(operatorName, id);
		if (1 != deleteCnt) {
			throw new BizException(BizCode.ABNORMAL_REPORT_ID_ERROR.getCode(), BizCode.ABNORMAL_REPORT_ID_ERROR.getMessage());
		}
		return deleteCnt;
	}
	
	/**
	 * 插入监控数据
	 * 这个方法是数据上报的核心入口
	 * 完整流程:
	 * 1. 转换Entity为PO对象
	 * 2. 查询设备信息(获取阈值)
	 * 3. 计算异常标志(abnormalFlag)
	 * 4. 回填异常标志到Entity（供Domain层使用）
	 * 5. 保存到MySQL(同步,保证数据一致性)
	 * 6. 异步保存到ClickHouse(不阻塞主流程)
	 *
	 * @param deviceDataEntity 设备上报的数据
	 */
	@Override
	public void addDeviceData(DeviceDataEntity deviceDataEntity) {
		// 1. 转换Entity为PO对象
		DeviceDataPO deviceDataPO = deviceDataConverter.entity2po(deviceDataEntity);
		
		// 2. 查询设备信息(包含阈值配置)
		DevicePO devicePO = deviceMapper.queryParameterLimitsBySN(deviceDataPO.getDeviceSN());
		if (ObjectUtils.isEmpty(devicePO)) {
			log.error("设备不存在: deviceSN={}", deviceDataPO.getDeviceSN());
			throw new BizException(BizCode.DEVICE_SN_ERROR);
		}
		
		// 3. 计算异常标志
		// computeAbnormalFlag方法会比较实际值和阈值,设置abnormalFlag
		deviceDataPO.computeAbnormalFlag(devicePO);
		deviceDataPO.setDeviceId(devicePO.getId());
		
		// 4. 回填异常标志到Entity（供Domain层使用）
		List<AbnormalFlagVO> abnormalFlags = AbnormalFlagVO.fromCodes(deviceDataPO.getAbnormalCodes());
		deviceDataEntity.setAbnormalFlags(abnormalFlags);
		deviceDataEntity.setDeviceId(devicePO.getId());
		deviceDataEntity.setProcessState(deviceDataPO.getProcessState());
		
		// 5. 保存到MySQL(同步)
		// 这是主要的数据存储,必须保证成功
		deviceDataMapper.insertDeviceData(deviceDataPO);
		log.debug("MySQL保存成功: deviceSN={}, abnormalFlag={}", deviceDataPO.getDeviceSN(), deviceDataPO.getAbnormalFlag());
		
		// 6. 异步保存到ClickHouse(可选)
		// 如果ClickHouse配置了,就异步写入
		// 即使ClickHouse写入失败,也不影响MySQL数据
//		if (clickHouseDataBuffer != null) {
//			try {
//				clickHouseDataBuffer.add(deviceDataPO);
//				log.debug("ClickHouse缓冲区添加成功: deviceSN={}", deviceDataPO.getDeviceSN());
//			} catch (Exception e) {
//				// ClickHouse写入失败不影响主流程
//				log.error("ClickHouse缓冲区添加失败: deviceSN={}", deviceDataPO.getDeviceSN(), e);
//			}
//		}
	}
	
}
