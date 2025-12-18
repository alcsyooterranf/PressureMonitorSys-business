package org.pms.infrastructure.adapter.port;

import org.pms.api.IPushRpcService;
import org.pms.types.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 报警消息主动推送服务Feign客户端
 * @create 2025/12/17
 */
@FeignClient(
		name = "ws-service",
		url = "${rpc.ws.url}",
		configuration = FeignConfig.class
)
public interface IAlterPushRpcClient extends IPushRpcService {
	
	/**
	 * 广播告警消息给所有在线用户
	 *
	 * @param alertData 告警数据
	 * @return 响应结果
	 */
	@Override
	@PostMapping("/rpc/ws/broadcast")
	Response<Void> broadcast(@RequestBody Map<String, Object> alertData);
	
	
	/**
	 * 推送告警消息给指定用户列表
	 *
	 * @param request 请求参数（包含userIds和alertData）
	 * @return 响应结果
	 */
	@Override
	@PostMapping("/rpc/ws/push/batch")
	Response<Void> pushToUsers(@RequestParam("userIds") Long[] userIds, @RequestBody Map<String, Object> request);
	
}
