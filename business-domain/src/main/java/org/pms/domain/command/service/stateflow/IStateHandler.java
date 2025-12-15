package org.pms.domain.command.service.stateflow;//package org.pms.domain.command.service.stateflow;
//
//import com.pms.api.dto.command.CommandState;
//import com.pms.types.HttpResponse;
//
///**
// * 状态流转处理接口
// * 所有方法的入参均为待处理的任务id和其当前状态
// * 每一个方法名对应一个抽象的状态(State)
// * 通过继承抽象类AbstractState可以限制不同状态流转的具体逻辑
// * 方法返回值表示状态流转是否成功
// */
//public interface IStateHandler {
//
//    HttpResponse<String> initialized(Long taskId, CommandState currentState);
//
//    HttpResponse<String> sent(Long taskId, CommandState currentState);
//
//    HttpResponse<String> delivered(Long taskId, CommandState currentState);
//
//    HttpResponse<String> completed(Long taskId, CommandState currentState);
//
//    HttpResponse<String> cancelled(Long taskId, CommandState currentState);
//
//}
