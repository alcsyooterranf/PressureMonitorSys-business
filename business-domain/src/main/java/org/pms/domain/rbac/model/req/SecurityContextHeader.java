package org.pms.domain.rbac.model.req;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.List;

/**
 * 已认证用户信息, 使用请求头传递, 发送给后端服务
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityContextHeader {

    private Long id;
    private String username;
    private List<String> authorities;
    public static SecurityContextHeader build(String base64Encode) throws JsonProcessingException {
        // 1. Base64 解码
        byte[] decodedBytes = Base64.getDecoder().decode(base64Encode);
        String jsonString = new String(decodedBytes);

        // 2. JSON 反序列化
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, SecurityContextHeader.class);
    }

}
