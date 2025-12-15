package org.pms.domain.command.service.stateflow.impl;//package org.pms.domain.command.service.stateflow.impl;
//
//import com.pms.types.HttpResponse;
//import org.pms.domain.command.service.stateflow.IStateHandler;
//import org.pms.domain.command.service.stateflow.StateConfig;
//import org.pms.domain.command.service.stateflow.event.*;
//import org.springframework.stereotype.Service;
//
//@Service
//public class StateHandlerImpl extends StateConfig implements IStateHandler {
//
//    public StateHandlerImpl(InitializedState initializedState, SentState sentState, DeliveredState deliveredState,
//                            CompletedState completedState, CancelledState cancelledState) {
//        super(initializedState, sentState, deliveredState, completedState, cancelledState);
//    }
//
//    @Override
//    public HttpResponse<String> initialized(Long taskId, CommandState currentState) {
//        return stateGroup.get(currentState).initialized(taskId, currentState);
//    }
//
//    @Override
//    public HttpResponse<String> sent(Long taskId, CommandState currentState) {
//        return stateGroup.get(currentState).sent(taskId, currentState);
//    }
//
//    @Override
//    public HttpResponse<String> delivered(Long taskId, CommandState currentState) {
//        return stateGroup.get(currentState).delivered(taskId, currentState);
//    }
//
//    @Override
//    public HttpResponse<String> completed(Long taskId, CommandState currentState) {
//        return stateGroup.get(currentState).completed(taskId, currentState);
//    }
//
//    @Override
//    public HttpResponse<String> cancelled(Long taskId, CommandState currentState) {
//        return stateGroup.get(currentState).cancelled(taskId, currentState);
//    }
//
//}
