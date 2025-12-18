package org.pms.infrastructure.adapter.converter;

import org.mapstruct.Mapper;
import org.pms.domain.command.model.entity.CommandExecutionEntity;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;
import org.pms.infrastructure.mapper.po.CommandExecutionPO;

import java.util.List;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令执行转换器
 * @create 2025/12/16
 */
@Mapper(componentModel = "spring")
public abstract class CommandExecutionConverter {
	
	/**
	 * PO -> Entity
	 */
	public abstract CommandExecutionEntity po2entity(CommandExecutionPO commandExecutionPO);
	
	/**
	 * PO列表 -> Entity列表
	 */
	public abstract List<CommandExecutionEntity> pos2entities(List<CommandExecutionPO> commandExecutionPOS);
	
	/**
	 * Entity -> PO
	 */
	public abstract CommandExecutionPO entity2po(CommandExecutionEntity commandExecutionEntity);
	
	/**
	 * CommandExecutionStatusVO -> Short
	 */
	protected Short statusVOToShort(CommandExecutionStatusVO statusVO) {
		return statusVO == null ? null : statusVO.getCode();
	}
	
	/**
	 * Short -> CommandExecutionStatusVO
	 */
	protected CommandExecutionStatusVO shortToStatusVO(Short status) {
		return CommandExecutionStatusVO.fromCode(status);
	}
	
}

