package org.pms.domain.devicedata.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AbnormalFlagVO {

    NORMAL(0b0000_0000, "正常"),  // 0
    OVER_PRESSURE(0b0000_0001, "超压"),   // 1
    UNDER_PRESSURE(0b0000_0010, "欠压"),  // 2
    OVER_TEMPERATURE(0b0000_0100, "超温"),    // 4
    OVER_PRESSURE_AND_OVER_TEMPERATURE(0b0000_0101, "超压, 超温"),    // 5
    UNDER_PRESSURE_AND_OVER_TEMPERATURE(0b0000_0110, "欠压, 超温"),    // 6
    UNDER_TEMPERATURE(0b0000_1000, "欠温"),      // 8
    OVER_PRESSURE_AND_UNDER_TEMPERATURE(0b0000_1001, "超压, 欠温"),    // 9
    UNDER_PRESSURE_AND_UNDER_TEMPERATURE(0b0000_1010, "欠压, 欠温"),    // 10
    OVER_VOLTAGE(0b0001_0000, "电压超压"),    // 16
    OVER_PRESSURE_AND_OVER_VOLTAGE(0b0001_0001, "超压, 电压超压"),    // 17
    UNDER_PRESSURE_AND_OVER_VOLTAGE(0b0001_0010, "欠压, 电压超压"),    // 18
    OVER_TEMPERATURE_AND_OVER_VOLTAGE(0b0001_0100, "超温, 电压超压"),    // 20
    OVER_PRESSURE_AND_OVER_TEMPERATURE_AND_OVER_VOLTAGE(0b0001_0101, "超压, 超温, 电压超压"),    // 21
    UNDER_PRESSURE_AND_OVER_TEMPERATURE_AND_OVER_VOLTAGE(0b0001_0110, "欠压, 超温, 电压超压"),    // 22
    UNDER_TEMPERATURE_AND_OVER_VOLTAGE(0b0001_1000, "欠温, 电压超压"),    // 24
    OVER_PRESSURE_AND_UNDER_TEMPERATURE_AND_OVER_VOLTAGE(0b0001_1001, "超压, 欠温, 电压超压"),    // 25
    UNDER_PRESSURE_AND_UNDER_TEMPERATURE_AND_OVER_VOLTAGE(0b0001_1010, "欠压, 欠温, 电压超压"),    // 26
    UNDER_VOLTAGE(0b0010_0000, "电压欠压"),    // 32
    OVER_PRESSURE_AND_UNDER_VOLTAGE(0b0010_0001, "超压, 电压欠压"),    // 33
    UNDER_PRESSURE_AND_UNDER_VOLTAGE(0b0010_0010, "欠压, 电压欠压"),    // 34
    OVER_TEMPERATURE_AND_UNDER_VOLTAGE(0b0010_0100, "超温, 电压欠压"),    // 36
    OVER_PRESSURE_AND_OVER_TEMPERATURE_AND_UNDER_VOLTAGE(0b0010_0101, "超压, 超温, 电压欠压"),    // 37
    UNDER_PRESSURE_AND_OVER_TEMPERATURE_AND_UNDER_VOLTAGE(0b0010_0110, "欠压, 超温, 电压欠压"),    // 38
    UNDER_TEMPERATURE_AND_UNDER_VOLTAGE(0b0010_1000, "欠温, 电压欠压"),    // 40
    OVER_PRESSURE_AND_UNDER_TEMPERATURE_AND_UNDER_VOLTAGE(0b0010_1001, "超压, 欠温, 电压欠压"),    // 41
    UNDER_PRESSURE_AND_UNDER_TEMPERATURE_AND_UNDER_VOLTAGE(0b0010_1010, "欠压, 欠温, 电压欠压");    // 42

    private final Integer code;
    private final String desc;

    /**
     * 根据枚举code获取枚举类对象
     *
     * @param value
     * @return
     */
    public static AbnormalFlagVO getEnumByInteger(Integer value) {
        return Arrays.stream(AbnormalFlagVO.values())
                .filter(flag -> Objects.equals(value, flag.getCode()))
                .findAny()
                .orElseThrow();
    }

}
