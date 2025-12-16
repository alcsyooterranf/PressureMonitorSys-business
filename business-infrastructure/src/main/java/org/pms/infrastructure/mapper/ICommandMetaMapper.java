package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pms.api.dto.req.CommandMetaQueryCondition;
import org.pms.infrastructure.mapper.po.CommandMetaPO;

import java.util.List;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据Mapper
 * @create 2025/12/15
 */
@Mapper
public interface ICommandMetaMapper {

	/**
	 * 插入指令元数据
	 *
	 * @param commandMetaPO 指令元数据PO
	 * @return 影响行数
	 */
	int insert(CommandMetaPO commandMetaPO);

	/**
	 * 根据ID更新指令元数据
	 *
	 * @param commandMetaPO 指令元数据PO
	 * @return 影响行数
	 */
	int updateById(CommandMetaPO commandMetaPO);

	/**
	 * 根据pipeline_id和service_identifier更新
	 *
	 * @param commandMetaPO 指令元数据PO
	 * @return 影响行数
	 */
	int updateByPipelineIdAndServiceIdentifier(CommandMetaPO commandMetaPO);

	/**
	 * 根据ID查询
	 *
	 * @param id 主键ID
	 * @return 指令元数据PO
	 */
	CommandMetaPO selectById(Long id);

	/**
	 * 根据pipeline_id和service_identifier查询
	 *
	 * @param pipelineId        管道ID
	 * @param serviceIdentifier 服务标识符
	 * @return 指令元数据PO
	 */
	CommandMetaPO selectByPipelineIdAndServiceIdentifier(@Param("pipelineId") Long pipelineId,
	                                                      @Param("serviceIdentifier") String serviceIdentifier);

	/**
	 * 条件查询（分页）
	 *
	 * @param condition 查询条件
	 * @return 指令元数据PO列表
	 */
	List<CommandMetaPO> selectByCondition(CommandMetaQueryCondition condition);

	/**
	 * 条件查询总数
	 *
	 * @param condition 查询条件
	 * @return 总数
	 */
	Long countByCondition(CommandMetaQueryCondition condition);

	/**
	 * 根据ID更新状态
	 *
	 * @param id     主键ID
	 * @param status 状态
	 * @return 影响行数
	 */
	int updateStatusById(@Param("id") Long id, @Param("status") Short status);

}
