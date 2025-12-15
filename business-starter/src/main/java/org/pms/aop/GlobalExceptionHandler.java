package org.pms.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pms.types.AppException;
import com.pms.types.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.pms.api.common.HttpResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * WHAT THE ZZZZEAL
 *
 * @author zeal
 * @version 1.0
 * @since 2024/5/28 下午4:04
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	/**
	 * 处理数据校验异常
	 *
	 * @param request 请求
	 * @param e       异常
	 * @return 异常信息
	 */
	@ExceptionHandler(value = {
			ConstraintViolationException.class, MethodArgumentNotValidException.class, BindException.class})
	public HttpResponse<String> validationExceptionHandler(HttpServletRequest request, Exception e) {
		if (e instanceof ConstraintViolationException) {
			log.error("异常信息: {}",
					((ConstraintViolationException) e).getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";")));
		} else if (e instanceof MethodArgumentNotValidException) {
			log.error("异常信息: {}",
					Objects.requireNonNull(((MethodArgumentNotValidException) e).getBindingResult().getFieldError()).getDefaultMessage());
		} else if (e instanceof BindException) {
			log.error("异常信息: {}",
					((BindException) e).getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining()));
		}
		AppException appException = new AppException(ResponseCode.ILLEGAL_PARAMETER, e);
		return HttpResponse.<String>builder()
				.code(appException.getCode())
				.message(appException.getMessage())
				.build();
	}
	
	/**
	 * 处理业务异常
	 *
	 * @param request 请求
	 * @param e       异常
	 * @return 异常信息
	 */
	@ExceptionHandler(value = AppException.class)
	public HttpResponse<String> bizExceptionHandler(HttpServletRequest request, AppException e) {
		log.error("异常代码: {}, 异常信息: {}", e.getCode(), e.getMessage());
		return HttpResponse.<String>builder()
				.code(e.getCode())
				.message(e.getMessage())
				.build();
	}
	
	/**
	 * 处理SQL异常
	 *
	 * @param request 请求
	 * @param e       异常, 这里仅捕获DataIntegrityViolationException异常,
	 *                因为DuplicateKeyException单继承了DataIntegrityViolationException
	 * @return 异常信息
	 */
	@ExceptionHandler(value = DataAccessException.class)
	public HttpResponse<String> sqlExceptionHandler(HttpServletRequest request, DataAccessException e) {
		AppException appException;
		if (e instanceof DataIntegrityViolationException) {
			appException = new AppException(ResponseCode.SQL_INDEX_DUPLICATE, e);
			log.error("异常代码: {}, 异常信息: {}", appException.getCode(), appException.getMessage());
		} else {
			appException = new AppException(ResponseCode.UN_ERROR, e);
			log.error("异常信息: {}, 原始异常: {}", e.getMessage(), e.getCause().getMessage());
		}
		return HttpResponse.<String>builder()
				.code(appException.getCode())
				.message(appException.getMessage())
				.build();
	}
	
	/**
	 * 处理Json转化异常
	 *
	 * @param request 请求
	 * @param e       异常
	 * @return 异常信息
	 */
	@ExceptionHandler(value = JsonProcessingException.class)
	public HttpResponse<String> jsonExceptionHandler(HttpServletRequest request, JsonProcessingException e) {
		AppException appException = new AppException(ResponseCode.JSON_PARSE_ERROR, e);
		log.error("异常代码: {}, 异常信息: {}", appException.getCode(), appException.getMessage());
		return HttpResponse.<String>builder()
				.code(appException.getCode())
				.message(appException.getMessage())
				.build();
	}
	
	/**
	 * 处理其他异常
	 *
	 * @param request 请求
	 * @param e       异常
	 * @return 异常信息
	 */
	@ExceptionHandler(value = Exception.class)
	public HttpResponse<String> jsonExceptionHandler(HttpServletRequest request, Exception e) {
		AppException appException = new AppException(ResponseCode.UN_ERROR, e);
		log.error("异常代码: {}, 异常信息: {}, 原始异常: {}", appException.getCode(), appException.getMessage(),
				appException.getCause().getMessage());
		return HttpResponse.<String>builder()
				.code(appException.getCode())
				.message(appException.getMessage())
				.build();
	}
	
}
