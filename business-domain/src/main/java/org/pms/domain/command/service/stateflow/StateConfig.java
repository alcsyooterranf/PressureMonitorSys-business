package org.pms.domain.command.service.stateflow;//package org.pms.domain.command.service.stateflow;
//
//import com.pms.api.dto.command.CommandState;
//import jakarta.annotation.PostConstruct;
//import org.pms.domain.command.service.stateflow.event.*;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 状态流转配置
// */
//public class StateConfig {
//
//    private final InitializedState initializedState;
//    private final SentState sentState;
//    private final DeliveredState deliveredState;
//    private final CompletedState completedState;
//    private final CancelledState cancelledState;
//
//    protected Map<CommandState, AbstractState> stateGroup = new ConcurrentHashMap<>();
//
//    public StateConfig(InitializedState initializedState, SentState sentState, DeliveredState deliveredState,
//                       CompletedState completedState, CancelledState cancelledState) {
//        this.initializedState = initializedState;
//        this.sentState = sentState;
//        this.deliveredState = deliveredState;
//        this.completedState = completedState;
//        this.cancelledState = cancelledState;
//    }
//
//    @PostConstruct
//    public void init() {
//        stateGroup.put(CommandState.INITIALIZED, initializedState);
//        stateGroup.put(CommandState.SENT, sentState);
//        stateGroup.put(CommandState.DELIVERED, deliveredState);
//        stateGroup.put(CommandState.COMPLETED, completedState);
//        stateGroup.put(CommandState.CANCELLED, cancelledState);
//    }
//
//}
