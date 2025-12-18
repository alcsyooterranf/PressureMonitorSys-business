package org.pms.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.pms.types.BizCode;
import org.pms.types.Response;
import org.pms.types.exception.BizException;
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
	public Response<String> validationExceptionHandler(HttpServletRequest request, Exception e) {
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
		BizException bizException = new BizException(BizCode.ILLEGAL_PARAMETER, e);
		return Response.<String>builder()
				.code(bizException.getCode())
				.message(bizException.getMessage())
				.build();
	}

	/**
	 * 处理业务异常
	 *
	 * @param request 请求
	 * @param e       异常
	 * @return 异常信息
	 */
	@ExceptionHandler(value = BizException.class)
	public Response<String> bizExceptionHandler(HttpServletRequest request, BizException e) {
		log.error("异常代码: {}, 异常信息: {}", e.getCode(), e.getMessage());
		return Response.<String>builder()
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
	public Response<String> sqlExceptionHandler(HttpServletRequest request, DataAccessException e) {
		BizException bizException;
		if (e instanceof DataIntegrityViolationException) {
			bizException = new BizException(BizCode.SQL_INDEX_DUPLICATE, e);
			log.error("异常代码: {}, 异常信息: {}", bizException.getCode(), bizException.getMessage());
		} else {
			bizException = new BizException(BizCode.UN_ERROR, e);
			log.error("异常信息: {}, 原始异常: {}", e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "null");
		}
		return Response.<String>builder()
				.code(bizException.getCode())
				.message(bizException.getMessage())
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
	public Response<String> jsonExceptionHandler(HttpServletRequest request, JsonProcessingException e) {
		BizException bizException = new BizException(BizCode.JSON_PARSE_ERROR, e);
		log.error("异常代码: {}, 异常信息: {}", bizException.getCode(), bizException.getMessage());
		return Response.<String>builder()
				.code(bizException.getCode())
				.message(bizException.getMessage())
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
	public Response<String> otherExceptionHandler(HttpServletRequest request, Exception e) {
		BizException bizException = new BizException(BizCode.UN_ERROR, e);
		log.error("异常代码: {}, 异常信息: {}, 原始异常: {}", bizException.getCode(), bizException.getMessage(),
				bizException.getCause() != null ? bizException.getCause().getMessage() : "null");
		return Response.<String>builder()
				.code(bizException.getCode())
				.message(bizException.getMessage())
				.build();
	}
	
}
