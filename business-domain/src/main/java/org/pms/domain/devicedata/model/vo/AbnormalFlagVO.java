package org.pms.domain.devicedata.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 设备异常标志枚举
 * 使用十进制数表示不同的异常状态，支持多个异常状态共存
 *
 * 设计说明：
 * - 每个异常类型对应一个独立的十进制数
 * - 多个异常可以同时存在，使用List<AbnormalFlagVO>表示
 * - code值用于数据库存储和传输
 *
 * @author refactor
 * @date 2025-12-17
 */
@Getter
@AllArgsConstructor
public enum AbnormalFlagVO {

    NORMAL(0, "正常"),
    OVER_PRESSURE(1, "超压"),
    UNDER_PRESSURE(2, "欠压"),
    OVER_TEMPERATURE(3, "超温"),
    UNDER_TEMPERATURE(4, "欠温"),
    OVER_VOLTAGE(5, "电压超压"),
    UNDER_VOLTAGE(6, "电压欠压");

    private final Integer code;
    private final String desc;

    /**
     * 根据枚举code获取枚举类对象
     *
     * @param value code值
     * @return 对应的枚举对象，如果不存在则返回NORMAL
     */
    public static AbnormalFlagVO getEnumByInteger(Integer value) {
        if (value == null) {
            return NORMAL;
        }
        return Arrays.stream(AbnormalFlagVO.values())
                .filter(flag -> Objects.equals(value, flag.getCode()))
                .findFirst()
                .orElse(NORMAL);
    }

    /**
     * 判断是否为异常状态
     *
     * @return true-异常，false-正常
     */
    public boolean isAbnormal() {
        return this != NORMAL;
    }

    /**
     * 将多个异常标志转换为描述字符串
     * 例如：[OVER_PRESSURE, OVER_TEMPERATURE] -> "超压, 超温"
     *
     * @param abnormalFlags 异常标志列表
     * @return 异常描述字符串
     */
    public static String toDescString(List<AbnormalFlagVO> abnormalFlags) {
        if (abnormalFlags == null || abnormalFlags.isEmpty()) {
            return NORMAL.getDesc();
        }
        return abnormalFlags.stream()
                .filter(AbnormalFlagVO::isAbnormal)
                .map(AbnormalFlagVO::getDesc)
                .collect(Collectors.joining(", "));
    }

    /**
     * 将多个异常标志转换为code列表
     * 例如：[OVER_PRESSURE, OVER_TEMPERATURE] -> [1, 3]
     *
     * @param abnormalFlags 异常标志列表
     * @return code列表
     */
    public static List<Integer> toCodes(List<AbnormalFlagVO> abnormalFlags) {
        if (abnormalFlags == null || abnormalFlags.isEmpty()) {
            return List.of(NORMAL.getCode());
        }
        return abnormalFlags.stream()
                .map(AbnormalFlagVO::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 从code列表转换为异常标志列表
     * 例如：[1, 3] -> [OVER_PRESSURE, OVER_TEMPERATURE]
     *
     * @param codes code列表
     * @return 异常标志列表
     */
    public static List<AbnormalFlagVO> fromCodes(List<Integer> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of(NORMAL);
        }
        return codes.stream()
                .map(AbnormalFlagVO::getEnumByInteger)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
