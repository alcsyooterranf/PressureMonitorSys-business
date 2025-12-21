package org.pms.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctg.ag.sdk.biz.AepDeviceCommandClient;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandRequest;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandResponse;
import org.junit.Test;
import org.pms.domain.command.model.valobj.CommandExecutionStatusVO;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description
 * @create 2025/12/20
 */
public class test {
	
	private final static String APP_KEY = "DpTC2bSbEy";
	private final static String APP_SECRET = "y75hAOrkls";
	private final static String MASTER_KEY = "f479f0206de648d4aa21d1e87d27c32c";
	
	@Test
	public void test1() {
		CommandExecutionStatusVO commandStatus = CommandExecutionStatusVO.valueOf("SAVED");
		System.out.println(commandStatus);
	}
	
	@Test
	public void testAepCommandSend() {
		// 构建请求体
		JSONObject content = new JSONObject();
		content.put("serviceIdentifier", "test_command");
		JSONObject params = JSON.parseObject("{\"syn\":\"tongbu\",\"test_attr\":11}");
		content.put("params", params);
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("deviceId", "1708754401");
		requestBody.put("productId", "17087544");
		requestBody.put("operator", "csy");
		requestBody.put("content", content);
		
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
			Long aepTaskId = parseResponse(response);
			System.out.println(aepTaskId);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("指令下发异常: " + e.getMessage(), e);
		} finally {
			client.shutdown();
		}
	}
	
	private Long parseResponse(CreateCommandResponse response) {
		// 解析响应
		JSONObject jsonResponse = JSON.parseObject(new String(response.getBody()));
		
		Integer code = jsonResponse.getInteger("code");
		if (code == null || code != 0) {
			String msg = jsonResponse.getString("msg");
			throw new RuntimeException("指令下发失败: " + msg);
		}
		
		// 获取commandId (AEP任务ID)
		JSONObject result = jsonResponse.getJSONObject("result");
		String commandId = result.getString("commandId");
		return Long.parseLong(commandId);
	}
	
}
