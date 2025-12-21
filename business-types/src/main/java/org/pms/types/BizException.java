package org.pms.types;

import lombok.Getter;

/**
 * 业务异常类
 * 继承RuntimeException，用于替代原有的AppException
 * 
 * <p>使用方式：
 * <pre>
 * // 方式1：使用BizCode枚举
 * throw new BizException(BizCode.DEVICE_SN_ERROR);
 * 
 * // 方式2：使用BizCode枚举 + 自定义消息
 * throw new BizException(BizCode.DEVICE_SN_ERROR, "设备SN: " + deviceSN + " 不存在");
 * 
 * // 方式3：使用错误码和消息
 * throw new BizException("ERR_BIZ_001", "自定义错误消息");
 * 
 * // 方式4：包装原始异常
 * throw new BizException(BizCode.JSON_PARSE_ERROR, e);
 * </pre>
 *
 * @author refactor
 * @date 2025-12-17
 */
@Getter
public class BizException extends RuntimeException {
	
	/**
	 * 错误码
	 */
	private final String code;
	
	/**
	 * 错误消息
	 */
	private final String message;
	
	/**
	 * 使用BizCode枚举构造异常
	 *
	 * @param bizCode 业务异常码枚举
	 */
	public BizException(BizCode bizCode) {
		super(bizCode.getMessage());
		this.code = bizCode.getCode();
		this.message = bizCode.getMessage();
	}
	
	/**
	 * 使用BizCode枚举 + 自定义消息构造异常
	 *
	 * @param bizCode 业务异常码枚举
	 * @param message 自定义错误消息
	 */
	public BizException(BizCode bizCode, String message) {
		super(message);
		this.code = bizCode.getCode();
		this.message = message;
	}
	
	/**
	 * 使用错误码和消息构造异常
	 *
	 * @param code    错误码
	 * @param message 错误消息
	 */
	public BizException(String code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}
	
	/**
	 * 使用BizCode枚举 + 原始异常构造异常
	 *
	 * @param bizCode 业务异常码枚举
	 * @param cause   原始异常
	 */
	public BizException(BizCode bizCode, Throwable cause) {
		super(bizCode.getMessage(), cause);
		this.code = bizCode.getCode();
		this.message = bizCode.getMessage();
	}
	
	/**
	 * 使用错误码、消息和原始异常构造异常
	 *
	 * @param code    错误码
	 * @param message 错误消息
	 * @param cause   原始异常
	 */
	public BizException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "BizException{" +
				"code='" + code + '\'' +
				", message='" + message + '\'' +
				'}';
	}
	
}

