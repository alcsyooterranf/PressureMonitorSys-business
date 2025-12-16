package org.pms.infrastructure.adapter;

import org.mapstruct.Mapper;
import org.pms.api.dto.req.CommandMetaInsertReq;
import org.pms.api.dto.req.CommandMetaUpdateReq;
import org.pms.api.dto.resp.CommandMetaQueryView;
import org.pms.domain.command.model.entity.CommandMetaEntity;
import org.pms.domain.command.model.valobj.StatusVO;
import org.pms.infrastructure.mapper.po.CommandMetaPO;

import java.util.List;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据转换器
 * @create 2025/12/15
 */
@Mapper(componentModel = "spring")
public abstract class CommandMetaConverter {

	/**
	 * 单个JavaBean转换 CommandMetaPO -> CommandMetaEntity
	 *
	 * @param commandMetaPO PO类
	 * @return Entity类
	 */
	public abstract CommandMetaEntity po2entity(CommandMetaPO commandMetaPO);

	/**
	 * 批量转换 List<CommandMetaPO> -> List<CommandMetaEntity>
	 *
	 * @param commandMetaPOS PO类列表
	 * @return Entity类列表
	 */
	public abstract List<CommandMetaEntity> pos2entities(List<CommandMetaPO> commandMetaPOS);

	/**
	 * 单个JavaBean转换 CommandMetaPO -> CommandMetaQueryView
	 *
	 * @param commandMetaPO PO
	 * @return CommandMetaQueryView
	 */
	public abstract CommandMetaQueryView po2view(CommandMetaPO commandMetaPO);

	/**
	 * 批量转换 List<CommandMetaPO> -> List<CommandMetaQueryView>
	 *
	 * @param commandMetaPOS PO列表
	 * @return List<CommandMetaQueryView>
	 */
	public abstract List<CommandMetaQueryView> pos2views(List<CommandMetaPO> commandMetaPOS);

	/**
	 * 单个JavaBean转换 CommandMetaInsertReq -> CommandMetaPO
	 *
	 * @param commandMetaInsertReq insertReq类
	 * @return PO类
	 */
	public abstract CommandMetaPO insertReq2po(CommandMetaInsertReq commandMetaInsertReq);

	/**
	 * 单个JavaBean转换 CommandMetaUpdateReq -> CommandMetaPO
	 *
	 * @param commandMetaUpdateReq updateReq类
	 * @return PO类
	 */
	public abstract CommandMetaPO updateReq2po(CommandMetaUpdateReq commandMetaUpdateReq);

	/**
	 * 单个JavaBean转换 CommandMetaEntity -> CommandMetaPO
	 *
	 * @param commandMetaEntity Entity类
	 * @return PO类
	 */
	public abstract CommandMetaPO entity2po(CommandMetaEntity commandMetaEntity);

	/**
	 * StatusVO枚举 -> Short
	 *
	 * @param statusVO 状态枚举
	 * @return Short状态码
	 */
	protected Short statusVOToShort(StatusVO statusVO) {
		return statusVO == null ? null : statusVO.getCode();
	}

	/**
	 * Short -> StatusVO枚举
	 *
	 * @param status 状态码
	 * @return StatusVO枚举
	 */
	protected StatusVO shortToStatusVO(Short status) {
		if (status == null) {
			return null;
		}
		for (StatusVO statusVO : StatusVO.values()) {
			if (statusVO.getCode().equals(status)) {
				return statusVO;
			}
		}
		return null;
	}

}

