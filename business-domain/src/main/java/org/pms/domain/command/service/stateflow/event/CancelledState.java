package org.pms.domain.command.service.stateflow.event;//package org.pms.domain.command.service.stateflow.event;
//
//import com.pms.api.dto.command.CommandState;
//import com.pms.types.HttpResponse;
//import com.pms.types.ResponseCode;
//import lombok.extern.slf4j.Slf4j;
//import org.pms.domain.command.repository.ICommandRepository;
//import org.pms.domain.command.service.stateflow.AbstractState;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class CancelledState extends AbstractState {
//
//    public CancelledState(ICommandRepository commandRepository) {
//        super(commandRepository);
//    }
//
//    @Override
//    public HttpResponse<String> initialized(Long taskId, CommandState currentState) {
//        log.error("取消状态不可初始化");
//        return HttpResponse.<String>builder()
//                .code(ResponseCode.COMMAND_STATE_ALTER_ILLEGAL.getCode())
//                .message(ResponseCode.COMMAND_STATE_ALTER_ILLEGAL.getMessage())
//                .build();
//    }
//
//    @Override
//    public HttpResponse<String> sent(Long taskId, CommandState currentState) {
//        log.error("取消状态不可发送");
//        return HttpResponse.<String>builder()
//                .code(ResponseCode.COMMAND_STATE_ALTER_ILLEGAL.getCode())
//                .message(ResponseCode.COMMAND_STATE_ALTER_ILLEGAL.getMessage())
//                .build();
//    }
//
//    @Override
//    public HttpResponse<String> delivered(Long taskId, CommandState currentState) {
//        log.error("取消状态不可送达");
//        return HttpResponse.<String>builder()
//                .code(ResponseCode.COMMAND_STATE_ALTER_ILLEGAL.getCode())
//                .message(ResponseCode.COMMAND_STATE_ALTER_ILLEGAL.getMessage())
//                .build();
//    }
//
//    @Override
//    public HttpResponse<String> completed(Long taskId, CommandState currentState) {
//        log.error("取消状态不可完成");
//        return HttpResponse.<String>builder()
//                .code(ResponseCode.COMMAND_STATE_ALTER_ILLEGAL.getCode())
//                .message(ResponseCode.COMMAND_STATE_ALTER_ILLEGAL.getMessage())
//                .build();
//    }
//
//    @Override
//    public HttpResponse<String> cancelled(Long taskId, CommandState currentState) {
//        log.error("取消状态不可重复取消");
//        return HttpResponse.<String>builder()
//                .code(ResponseCode.COMMAND_STATE_ALTER_ILLEGAL.getCode())
//                .message(ResponseCode.COMMAND_STATE_ALTER_ILLEGAL.getMessage())
//                .build();
//    }
//
//}
