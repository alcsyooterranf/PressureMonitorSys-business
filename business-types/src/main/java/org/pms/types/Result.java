package org.pms.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 统一返回对象
 * @create 2025/12/19
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Result {
	
	private String code;
	private String message;
	
	public static Result buildResult(BizCode code) {
		return new Result(code.getCode(), code.getMessage());
	}
	
	public static Result buildResult(BizCode code, String message) {
		return new Result(code.getCode(), message);
	}
	
	public static Result buildResult(String code, String message) {
		return new Result(code, message);
	}
	
	public static Result buildSuccessResult() {
		return new Result(BizCode.SUCCESS.getCode(), BizCode.SUCCESS.getMessage());
	}
	
	public static Result buildErrorResult() {
		return new Result(BizCode.UN_ERROR.getCode(), BizCode.UN_ERROR.getMessage());
	}
	
	public static Result buildErrorResult(String message) {
		return new Result(BizCode.UN_ERROR.getCode(), message);
	}
	
	public boolean isSuccess() {
		return BizCode.SUCCESS.getCode().equals(this.code);
	}
	
	public boolean isError() {
		return !isSuccess();
	}
	
}
