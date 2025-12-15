package org.pms.infrastructure.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDataPO {

    private Long id;
    private String tenantId;
    private String serviceId;
    private String protocol;
    private Long pipelineId;
    private String pipelineSN;
    private Integer temperature;
    private Integer voltage;
    private Integer pressure;
    private Short abnormalFlag;
    private String messageType;
    private String deviceType;
    private Long deviceId;
    private String deviceSN;
    private String assocAssetId;
    private String IMSI;
    private String IMEI;
    private Date createTime;
    private Date processTime;
    private String processBy;
    private Date deleteTime;
    private String deleteBy;
    private Boolean removed;
    private Boolean processState;

    public void computeAbnormalFlag(DevicePO devicePO) {
        // 初始化异常标志
        this.abnormalFlag = 0;
        // 从高位到低位逐位判断
        if (this.voltage < devicePO.getVoltageLowerBound()) {
			this.abnormalFlag = (short) (this.abnormalFlag | (1 << 5));
		}
        if (this.voltage > devicePO.getVoltageUpperBound()) {
			this.abnormalFlag = (short) (this.abnormalFlag | (1 << 4));
		}
        if (this.temperature < devicePO.getTemperatureLowerBound()) {
			this.abnormalFlag = (short) (this.abnormalFlag | (1 << 3));
		}
        if (this.temperature > devicePO.getTemperatureUpperBound()) {
			this.abnormalFlag = (short) (this.abnormalFlag | (1 << 2));
		}
        if (this.pressure < devicePO.getPressureLowerBound()) {
			this.abnormalFlag = (short) (this.abnormalFlag | (1 << 1));
		}
        if (this.pressure > devicePO.getPressureUpperBound()) {
			this.abnormalFlag = (short) (this.abnormalFlag | 1);
		}

        // 重写处理状态
        this.processState = this.abnormalFlag == 0;
    }

}
