package org.pms.infrastructure.repository.persistence;//package org.pms.infrastructure.repository;
//
//import org.pms.domain.command.model.valobj.AlterStateVO;
//import org.pms.domain.command.repository.ICommandRepository;
//import org.pms.infrastructure.mapper.ICommandMapper;
//import org.pms.infrastructure.mapper.IDeviceMapper;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class CommandRepository implements ICommandRepository {
//
//    private final ICommandMapper commandMapper;
//    private final IDeviceMapper deviceMapper;
//
//    public CommandRepository(ICommandMapper commandMapper, IDeviceMapper deviceMapper) {
//        this.commandMapper = commandMapper;
//        this.deviceMapper = deviceMapper;
//    }
//
//    @Override
//    public boolean alterStatus(Long taskId, CommandState beforeState, CommandState afterState) {
//        AlterStateVO alterStateVO = new AlterStateVO(taskId, beforeState, afterState);
//        int cnt = commandMapper.alterState(alterStateVO);
//        return 1 == cnt;
//    }
//
//    @Override
//    public boolean checkCommandDestinationIds(Long deviceSN, Long productSN) {
//        return deviceMapper.checkDeviceSNAndProductSN(deviceSN, productSN) != null;
//    }
//
//}
