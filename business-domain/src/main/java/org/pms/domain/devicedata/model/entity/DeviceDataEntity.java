package org.pms.domain.devicedata.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pms.domain.devicedata.model.vo.AbnormalFlagVO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDataEntity {

    private Long id;
    private String tenantId;
	private String protocol;
    private Long pipelineId;
    private String pipelineSN;
    private Integer temperature;
    private Integer voltage;
    private Integer pressure;

    /**
     * 异常标志列表（支持多个异常共存）
     * 例如：设备可能同时存在超压和超温
     */
    @Builder.Default
    private List<AbnormalFlagVO> abnormalFlags = new ArrayList<>();

    private Long deviceId;
    private String deviceSN;
    private Date createTime;
    private Date processTime;
    private String processBy;
    private Date deleteTime;
    private String deleteBy;
    private Boolean removed;
    private Boolean processState;

    /**
     * 判断是否存在异常
     *
     * @return true-存在异常，false-正常
     */
    public boolean hasAbnormal() {
        return abnormalFlags != null
                && !abnormalFlags.isEmpty()
                && abnormalFlags.stream().anyMatch(AbnormalFlagVO::isAbnormal);
    }

    /**
     * 获取异常描述字符串
     *
     * @return 异常描述，例如："超压, 超温"
     */
    public String getAbnormalDesc() {
        return AbnormalFlagVO.toDescString(abnormalFlags);
    }

}
