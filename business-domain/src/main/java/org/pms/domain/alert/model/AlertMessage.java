package org.pms.domain.alert.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 告警消息DTO
 * 
 * 这个类用于封装告警消息,通过WebSocket发送到前端
 * 前端收到这个JSON对象后,解析并显示告警信息
 * 
 * JSON示例:
 * {
 *   "id": 12345,
 *   "deviceSN": "10001",
 *   "productId": 1,
 *   "productSN": "P001",
 *   "pressure": 150,
 *   "pressureUpperBound": 100,
 *   "temperature": 2500,
 *   "voltage": 3000,
 *   "abnormalDesc": "压力超上限",
 *   "alertTime": "2024-11-24 10:30:00",
 *   "alertType": "PRESSURE_HIGH",
 *   "severity": "CRITICAL"
 * }
 * 
 * @author zeal
 * @since 2024-11-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertMessage {

    /**
     * 监控数据ID
     * 用于前端跳转到详情页,或者标记已处理
     */
    private Long id;

    /**
     * 设备序列号
     */
    private String deviceSN;

    /**
     * 管道ID
     */
    private Long pipelineId;

    /**
     * 管道序列号
     */
    private String pipelineSN;

    /**
     * 当前压力值(Pa)
     */
    private Integer pressure;

    /**
     * 压力上限阈值(Pa)
     */
    private Integer pressureUpperBound;

    /**
     * 压力下限阈值(Pa)
     */
    private Integer pressureLowerBound;

    /**
     * 当前温度值(单位:0.01℃)
     * 例如:2500表示25.00℃
     */
    private Integer temperature;

    /**
     * 温度上限阈值(单位:0.01℃)
     */
    private Integer temperatureUpperBound;

    /**
     * 温度下限阈值(单位:0.01℃)
     */
    private Integer temperatureLowerBound;

    /**
     * 当前电压值(mV)
     */
    private Integer voltage;

    /**
     * 电压上限阈值(mV)
     */
    private Integer voltageUpperBound;

    /**
     * 电压下限阈值(mV)
     */
    private Integer voltageLowerBound;

    /**
     * 异常描述
     * 例如:"压力超上限"、"温度低于下限"等
     */
    private String abnormalDesc;

    /**
     * 告警时间
     * @JsonFormat注解用于格式化日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date alertTime;

    /**
     * 告警类型
     * 
     * 可选值:
     * - PRESSURE_HIGH: 压力超上限
     * - PRESSURE_LOW: 压力低于下限
     * - TEMPERATURE_HIGH: 温度超上限
     * - TEMPERATURE_LOW: 温度低于下限
     * - VOLTAGE_HIGH: 电压超上限
     * - VOLTAGE_LOW: 电压低于下限
     */
    private String alertType;

    /**
     * 严重程度
     * 
     * 可选值:
     * - CRITICAL: 严重(红色,需要立即处理)
     * - WARNING: 警告(橙色,需要关注)
     * - INFO: 信息(蓝色,仅提示)
     * 
     * 前端可以根据严重程度显示不同颜色的告警
     */
    private String severity;

    /**
     * 设备位置(可选)
     * 方便用户快速定位设备
     */
    private String location;

    /**
     * 设备经度(可选)
     */
    private String longitude;

    /**
     * 设备纬度(可选)
     */
    private String latitude;
}

