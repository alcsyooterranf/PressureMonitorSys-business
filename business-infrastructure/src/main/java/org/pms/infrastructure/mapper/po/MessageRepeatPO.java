package org.pms.infrastructure.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRepeatPO {

    /**
     * 自增ID
     */
    private Long id;
    /**
     * 消息key
     */
    private String key;
    /**
     * 删除标记
     */
    private Boolean is_deleted;

}
