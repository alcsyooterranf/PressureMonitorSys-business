package org.pms.domain.command.service.impl;//package org.pms.domain.command.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.ctg.ag.sdk.biz.AepDeviceCommandClient;
//import com.ctg.ag.sdk.biz.AepDeviceModelClient;
//import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandRequest;
//import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandResponse;
//import com.ctg.ag.sdk.biz.aep_device_model.QueryServiceListRequest;
//import com.ctg.ag.sdk.biz.aep_device_model.QueryServiceListResponse;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.pms.domain.command.model.dto.ParameterDTO;
//import org.pms.domain.command.model.dto.PropertyDTO;
//import org.pms.domain.command.model.dto.ServiceDTO;
//import org.pms.domain.command.model.res.CommandQueryRes;
//import org.pms.domain.command.model.sdk.queryService.SDKQueryServiceDTO;
//import org.pms.domain.command.service.ICommandService;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Slf4j
//@Service
//public class CommandService implements ICommandService {
//
//    private final RedisTemplate<Object, Object> redisTemplate;
//    @Value("${app.sdk.master-key}")
//    private String MASTER_KEY;
//    @Value("${app.sdk.app-key}")
//    private String APP_KEY;
//    @Value("${app.sdk.app-secret}")
//    private String APP_SECRET;
//    @Value("${app.sdk.command-type}")
//    private Integer COMMAND_TYPE;
//
//    public CommandService(RedisTemplate<Object, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    @Override
//    public CommandQueryRes queryCommandBySDK(Long productId, Long pageNow, Long pageSize) {
//        AepDeviceModelClient client = AepDeviceModelClient.newClient()
//                .appKey(APP_KEY).appSecret(APP_SECRET)
//                .build();
//        QueryServiceListRequest request = new QueryServiceListRequest();
//        // set your request params here
//        request.setParamMasterKey(MASTER_KEY);
//        request.setParamProductId(productId);
//        request.setParamServiceType(COMMAND_TYPE);
//        request.setParamPageNow(pageNow);
//        request.setParamPageSize(pageSize);
//        QueryServiceListResponse response;
//        try {
//            response = client.QueryServiceList(request);
//        } catch (Exception e) {
//            log.error("sdk调用失败");
//            throw new RuntimeException(e);
//        }
//        client.shutdown();
//        ObjectMapper objectMapper = new ObjectMapper();
//        SDKQueryServiceDTO sdkQueryServiceDTO;
//        try {
//            sdkQueryServiceDTO = objectMapper.readValue(response.getBody(), SDKQueryServiceDTO.class);
//        } catch (IOException e) {
//            log.error("SDKQueryServiceDTO类型转换错误");
//            throw new RuntimeException(e);
//        }
//        SDKResultDTO result = sdkQueryServiceDTO.getResult();
//        if (result == null) {
//			return null;
//		}
//        return CommandQueryRes.builder()
//                .pageNum(result.getPageNum())
//                .pageSize(result.getPageSize())
//                .total(result.getTotal())
//                .list(result.getList().stream().map(sdkServiceDTO -> ServiceDTO.<PropertyDTO, ParameterDTO>builder()
//                        .serviceId(sdkServiceDTO.getServiceId())
//                        .serviceFlag(sdkServiceDTO.getServiceFlag())
//                        .serviceName(sdkServiceDTO.getServiceName())
//                        .serviceType(sdkServiceDTO.getServiceType())
//                        .eventType(sdkServiceDTO.getEventType())
//                        .description(sdkServiceDTO.getDescription())
//                        .properties(sdkServiceDTO.getProperties().stream().map(sdkPropertyDTO ->
//                                PropertyDTO.builder()
//                                        .propertyFlag(sdkPropertyDTO.getPropertyFlag())
//                                        .propertyName(sdkPropertyDTO.getPropertyName())
//                                        .dataType(sdkPropertyDTO.getDataType())
//                                        .unit(sdkPropertyDTO.getUnit())
//                                        .description(sdkPropertyDTO.getDescription())
//                                        .build()).toList())
//                        .parameters(sdkServiceDTO.getParameters().stream().map(sdkParameterDTO ->
//                                ParameterDTO.builder()
//                                        .parameterFlag(sdkParameterDTO.getParameterFlag())
//                                        .parameterName(sdkParameterDTO.getParameterName())
//                                        .dataType(sdkParameterDTO.getDataType())
//                                        .unit(sdkParameterDTO.getUnit())
//                                        .description(sdkParameterDTO.getDescription())
//                                        .build()).toList())
//                        .build()).toList())
//                .build();
//    }
//
//    @Override
//    public String createCommandBySDK(String json) {
//        JSONObject jsonRequest = JSON.parseObject(json);
//        JSONObject content = jsonRequest.getJSONObject("content");
//        if (content.isEmpty()) {
//            log.error("content为空");
//            return "content为空";
//        }
//        // 用于校验和redis存储
//        String deviceSN = jsonRequest.getString("deviceSN");
//        jsonRequest.remove("deviceSN");
//        jsonRequest.putIfAbsent("deviceId", deviceSN);
//        // 用于存储操作人员
//        String operator = jsonRequest.getString("operator");
//        // 用于校验
//        String productSN = jsonRequest.getString("productSN");
//        jsonRequest.remove("productSN");
//        jsonRequest.putIfAbsent("productId", Long.parseLong(productSN));
//
//        JSONObject param = content.getJSONObject("params");
//        if (param.isEmpty()) {
//            log.error("params为空");
//            return "params为空";
//        }
//        // 用于redis存储
//        String syn = param.getString("syn");
//        // 用于redis存储
//        String serviceIdentifier = content.getString("serviceIdentifier");
//
//        // TODO: 开启多线程发送请求, 从而实现异步等待
//        AepDeviceCommandClient client = AepDeviceCommandClient.newClient()
//                .appKey(APP_KEY).appSecret(APP_SECRET)
//                .build();
//
//        CreateCommandRequest request = new CreateCommandRequest();
//        request.setParamMasterKey(MASTER_KEY);
//        request.setBody(jsonRequest.toJSONString().getBytes());
//        CreateCommandResponse response = null;
//        try {
//            response = client.CreateCommand(request);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        client.shutdown();
//
//        // 解析从AEP平台返回的json串
//        JSONObject jsonResponse = JSON.parseObject(new String(response.getBody()));
//        System.out.println(jsonResponse);
//        Integer code = jsonResponse.getInteger("code");
//        if (0 != code) {
//            log.error("指令下发失败");
//            return "failure";
//        }
//        String commandId = jsonResponse.getJSONObject("result").getString("commandId");
//        Long productId = jsonResponse.getJSONObject("result").getLong("productId");
//        String deviceId = jsonResponse.getJSONObject("result").getString("deviceId");
//
//        return "success";
//    }
//
//}
