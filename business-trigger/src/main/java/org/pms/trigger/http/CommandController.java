package org.pms.trigger.http;//package org.pms.trigger.http;
//
//import com.pms.types.HttpResponse;
//import com.pms.types.ResponseCode;
//import lombok.extern.slf4j.Slf4j;
//import org.pms.domain.command.model.res.CommandQueryRes;
//import org.pms.domain.command.service.ICommandService;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//@Slf4j
//@Validated
//@RestController
//@CrossOrigin("${app.config.cross-origin}")
//@RequestMapping("/command/")
//public class CommandController {
//
//    private final ICommandService commandService;
//
//    public CommandController(ICommandService commandService) {
//        this.commandService = commandService;
//    }
//
//    @RequestMapping(value = "query_command", method = RequestMethod.POST)
//    public HttpResponse<CommandQueryRes> queryCommandPage(@RequestParam("productSN") Long productId,
//                                                      @RequestParam("pageNum") Long pageNow,
//                                                      @RequestParam("pageSize") Long pageSize) {
//        CommandQueryRes commandQueryRes = commandService.queryCommandBySDK(productId, pageNow, pageSize);
//
//        return HttpResponse.<CommandQueryRes>builder()
//                .code(ResponseCode.SUCCESS.getCode())
//                .message(ResponseCode.SUCCESS.getMessage())
//                .data(commandQueryRes)
//                .build();
//    }
//
//    @RequestMapping(value = "create_command", method = RequestMethod.POST)
//    public HttpResponse<String> createCommand(@RequestBody String json)  {
//        String commandBySDK = commandService.createCommandBySDK(json);
//        return HttpResponse.<String>builder()
//                .code(ResponseCode.SUCCESS.getCode())
//                .message(ResponseCode.SUCCESS.getMessage())
//                .data(commandBySDK)
//                .build();
//    }
//
//}
