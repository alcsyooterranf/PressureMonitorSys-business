package org.pms.domain.command.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pms.domain.command.model.entity.CommandExecutionEntity;
import org.pms.domain.command.model.entity.CommandMetaEntity;
import org.pms.domain.command.model.entity.CommandTaskEntity;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令聚合对象
 * @create 2025/12/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandAggregate {
	
	private CommandExecutionEntity commandExecutionEntity;
	private CommandTaskEntity commandTaskEntity;
	private CommandMetaEntity commandMetaEntity;
	
}
