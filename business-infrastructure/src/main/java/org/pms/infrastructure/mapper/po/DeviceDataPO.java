package org.pms.infrastructure.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDataPO {

    private Long id;
    private String tenantId;
    private String protocol;
    private Long pipelineId;
    private String pipelineSN;
    private Integer temperature;
    private Integer voltage;
    private Integer pressure;

    /**
     * 异常标志（存储格式：逗号分隔的code，例如："1,3" 表示超压和超温）
     * 兼容旧版本：如果只有一个异常，可以直接存储数字
     */
    private String abnormalFlag;
	
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

    /**
     * 计算异常标志
     * 检查所有可能的异常状态，支持多个异常共存
     *
     * @param devicePO 设备信息（包含阈值）
     */
    public void computeAbnormalFlag(DevicePO devicePO) {
        List<Integer> abnormalCodes = new ArrayList<>();

        // 1. 检查压力异常
        if (this.pressure != null && devicePO.getPressureUpperBound() != null
                && this.pressure > devicePO.getPressureUpperBound()) {
            abnormalCodes.add(1); // 超压
        }
        if (this.pressure != null && devicePO.getPressureLowerBound() != null
                && this.pressure < devicePO.getPressureLowerBound()) {
            abnormalCodes.add(2); // 欠压
        }

        // 2. 检查温度异常
        if (this.temperature != null && devicePO.getTemperatureUpperBound() != null
                && this.temperature > devicePO.getTemperatureUpperBound()) {
            abnormalCodes.add(3); // 超温
        }
        if (this.temperature != null && devicePO.getTemperatureLowerBound() != null
                && this.temperature < devicePO.getTemperatureLowerBound()) {
            abnormalCodes.add(4); // 欠温
        }

        // 3. 检查电压异常
        if (this.voltage != null && devicePO.getVoltageUpperBound() != null
                && this.voltage > devicePO.getVoltageUpperBound()) {
            abnormalCodes.add(5); // 电压超压
        }
        if (this.voltage != null && devicePO.getVoltageLowerBound() != null
                && this.voltage < devicePO.getVoltageLowerBound()) {
            abnormalCodes.add(6); // 电压欠压
        }

        // 4. 设置异常标志（逗号分隔的code字符串）
        if (abnormalCodes.isEmpty()) {
            this.abnormalFlag = "0"; // 正常
        } else {
            this.abnormalFlag = abnormalCodes.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }

        // 5. 设置处理状态：abnormalFlag为"0"表示正常，无需处理
        this.processState = "0".equals(this.abnormalFlag);
    }

    /**
     * 获取异常code列表
     *
     * @return 异常code列表
     */
    public List<Integer> getAbnormalCodes() {
        if (abnormalFlag == null || abnormalFlag.isEmpty() || "0".equals(abnormalFlag)) {
            return List.of(0);
        }

        List<Integer> codes = new ArrayList<>();
        for (String code : abnormalFlag.split(",")) {
            try {
                codes.add(Integer.parseInt(code.trim()));
            } catch (NumberFormatException e) {
                // 忽略无效的code
            }
        }
        return codes.isEmpty() ? List.of(0) : codes;
    }

}
