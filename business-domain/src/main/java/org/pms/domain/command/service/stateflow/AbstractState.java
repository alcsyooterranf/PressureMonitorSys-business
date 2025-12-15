package org.pms.domain.command.service.stateflow;//package org.pms.domain.command.service.stateflow;
//
//import com.pms.api.dto.command.CommandState;
//import com.pms.types.HttpResponse;
//import org.pms.domain.command.repository.ICommandRepository;
//
//public abstract class AbstractState {
//
//    protected final ICommandRepository commandRepository;
//
//    protected AbstractState(ICommandRepository commandRepository) {
//        this.commandRepository = commandRepository;
//    }
//
//    public abstract HttpResponse<String> initialized(Long taskId, CommandState currentState);
//
//    public abstract HttpResponse<String> sent(Long taskId, CommandState currentState);
//
//    public abstract HttpResponse<String> delivered(Long taskId, CommandState currentState);
//
//    public abstract HttpResponse<String> completed(Long taskId, CommandState currentState);
//
//    public abstract HttpResponse<String> cancelled(Long taskId, CommandState currentState);
//
//}
