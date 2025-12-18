package org.pms.domain.command.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctg.ag.sdk.biz.AepDeviceCommandClient;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandRequest;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandResponse;
import com.ctg.ag.sdk.core.model.ApiCallBack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description AEP设备指令客户端封装
 * @create 2025/12/16
 */
@Slf4j
@Component
public class AepCommandClient {
	
	@Value("${app.sdk.master-key}")
	private String MASTER_KEY;
	
	@Value("${app.sdk.app-key}")
	private String APP_KEY;
	
	@Value("${app.sdk.app-secret}")
	private String APP_SECRET;
	
	/**
	 * 同步发送指令到AEP平台
	 *
	 * @param deviceSN          设备SN
	 * @param pipelineId        管道ID
	 * @param serviceIdentifier 服务标识符
	 * @param params            参数（JSON对象）
	 * @return AEP任务ID (commandId)
	 */
	public Long sendCommand(Long deviceSN, Long pipelineId, String serviceIdentifier, JSONObject params) {
		// 构建请求体
		JSONObject content = new JSONObject();
		content.put("serviceIdentifier", serviceIdentifier);
		content.put("params", params);
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("deviceId", deviceSN);
		requestBody.put("productId", pipelineId);
		// TODO: 入参带入operator
		requestBody.put("operator", "csy");
		requestBody.put("content", content);
		
		log.info("准备发送指令到AEP平台, deviceSN={}, pipelineId={}, serviceIdentifier={}, params={}",
				deviceSN, pipelineId, serviceIdentifier, params);
		
		// 创建AEP客户端
		AepDeviceCommandClient client = AepDeviceCommandClient.newClient()
				.appKey(APP_KEY)
				.appSecret(APP_SECRET)
				.build();
		
		try {
			// 创建请求
			CreateCommandRequest request = new CreateCommandRequest();
			request.setParamMasterKey(MASTER_KEY);
			request.setBody(requestBody.toJSONString().getBytes());
			
			// 发送请求
			CreateCommandResponse response = client.CreateCommand(request);
			
			// 解析响应并返回commandId
			return parseResponse(response);
			
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			log.error("指令下发异常", e);
			throw new RuntimeException("指令下发异常: " + e.getMessage(), e);
		} finally {
			client.shutdown();
		}
	}
	
	/**
	 * 异步发送指令到AEP平台（使用CompletableFuture封装）
	 * SDK的CreateCommand方法签名：
	 * Future<CreateCommandResponse> CreateCommand(CreateCommandRequest request, ApiCallBack<CreateCommandRequest, CreateCommandResponse> callback)
	 *
	 * @param deviceSN          设备SN
	 * @param pipelineId        管道ID
	 * @param serviceIdentifier 服务标识符
	 * @param params            参数（JSON对象）
	 * @return CompletableFuture<Long> 异步返回AEP任务ID
	 */
	public CompletableFuture<Long> sendCommandAsync(Long deviceSN, Long pipelineId, String serviceIdentifier, JSONObject params) {
		// 构建请求体
		JSONObject content = new JSONObject();
		content.put("serviceIdentifier", serviceIdentifier);
		content.put("params", params);
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("deviceId", deviceSN);
		requestBody.put("productId", pipelineId);
		requestBody.put("content", content);
		
		log.info("准备异步发送指令到AEP平台, deviceSN={}, pipelineId={}, serviceIdentifier={}, params={}",
				deviceSN, pipelineId, serviceIdentifier, params);
		
		// 创建AEP客户端
		AepDeviceCommandClient client = AepDeviceCommandClient.newClient()
				.appKey(APP_KEY)
				.appSecret(APP_SECRET)
				.build();
		
		// 创建请求
		CreateCommandRequest request = new CreateCommandRequest();
		request.setParamMasterKey(MASTER_KEY);
		request.setBody(requestBody.toJSONString().getBytes());
		
		// 创建CompletableFuture用于异步处理
		CompletableFuture<Long> completableFuture = new CompletableFuture<>();
		
		try {
			// 调用SDK的异步方法，传入request和ApiCallBack
			// SDK方法签名：Future<CreateCommandResponse> CreateCommand(CreateCommandRequest request, ApiCallBack<CreateCommandRequest, CreateCommandResponse> callback)
			client.CreateCommand(request, new ApiCallBack<CreateCommandRequest, CreateCommandResponse>() {
				
				@Override
				public void onFailure(CreateCommandRequest req, Exception exception) {
					// 异步调用失败时的回调
					log.error("AEP SDK异步调用失败, deviceSN={}, pipelineId={}, serviceIdentifier={}",
							deviceSN, pipelineId, serviceIdentifier, exception);
					
					// 关闭客户端
					client.shutdown();
					
					// 完成CompletableFuture并传递异常
					completableFuture.completeExceptionally(
							new RuntimeException("异步指令下发失败: " + exception.getMessage(), exception)
					);
				}
				
				@Override
				public void onResponse(CreateCommandRequest req, CreateCommandResponse response) {
					// 异步调用成功时的回调
					try {
						log.info("AEP SDK异步调用成功, deviceSN={}, pipelineId={}, serviceIdentifier={}",
								deviceSN, pipelineId, serviceIdentifier);
						
						// 解析响应获取commandId
						Long commandId = parseResponse(response);
						
						// 完成CompletableFuture并传递结果
						completableFuture.complete(commandId);
						
					} catch (Exception e) {
						log.error("解析AEP响应失败", e);
						completableFuture.completeExceptionally(
								new RuntimeException("解析AEP响应失败: " + e.getMessage(), e)
						);
					} finally {
						// 关闭客户端
						client.shutdown();
					}
				}
			});
			
			return completableFuture;
			
		} catch (Exception e) {
			log.error("创建异步请求失败", e);
			client.shutdown();
			completableFuture.completeExceptionally(
					new RuntimeException("创建异步请求失败: " + e.getMessage(), e)
			);
			return completableFuture;
		}
	}
	
	/**
	 * 解析AEP响应
	 *
	 * @param response AEP响应
	 * @return commandId (AEP任务ID)
	 */
	private Long parseResponse(CreateCommandResponse response) {
		// 解析响应
		JSONObject jsonResponse = JSON.parseObject(new String(response.getBody()));
		log.info("AEP平台响应: {}", jsonResponse);
		
		Integer code = jsonResponse.getInteger("code");
		if (code == null || code != 0) {
			String msg = jsonResponse.getString("msg");
			log.error("指令下发失败, code={}, msg={}", code, msg);
			throw new RuntimeException("指令下发失败: " + msg);
		}
		
		// 获取commandId (AEP任务ID)
		JSONObject result = jsonResponse.getJSONObject("result");
		String commandId = result.getString("commandId");
		
		log.info("指令下发成功, commandId={}", commandId);
		return Long.parseLong(commandId);
	}
	
}

