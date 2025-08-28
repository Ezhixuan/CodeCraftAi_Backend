package com.ezhixuan.codeCraftAi_backend.exception;

import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.domain.constant.UserConstant;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
@Hidden
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public BaseResponse<String> methodArgumentNotValidExceptionHandler(
      MethodArgumentNotValidException e) {
    log.error("exception: {}", e.getMessage(), e);
    log.error(
        "methodArgumentNotValidException: {}",
        Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    BindingResult bindingResult = e.getBindingResult();
    String errorMsg =
        bindingResult.getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("参数错误");
    return R.error(ErrorCode.PARAMS_ERROR, errorMsg);
  }

  @ExceptionHandler(BusinessException.class)
  public BaseResponse<String> businessExceptionHandler(BusinessException e) {
    if (String.valueOf(e.getCode()).startsWith("401")) {
      // 需要检查是否存在登录态,如果存在需要清除
      RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
      HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
      request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
    }
    log.error("exception: {}", e.getMessage(), e);
    return R.error(e.getCode(), e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public BaseResponse<String> exceptionHandler(Exception e) {
    log.error("exception: {}", e.getMessage(), e);
    return R.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
  }
}
