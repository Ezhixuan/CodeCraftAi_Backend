package com.ezhixuan.ai.codeCraftAi_backend.exception;

import java.util.Objects;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ezhixuan.ai.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.ai.codeCraftAi_backend.common.R;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("methodArgumentNotValidException: {}",
            Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
        BindingResult bindingResult = e.getBindingResult();
        String errorMsg = bindingResult.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
            .findFirst().orElse("参数错误");
        return R.error(ErrorCode.PARAMS_ERROR, errorMsg);
    }

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<?> exceptionHandler(Exception e) {
        log.error("exception: {}", e.getMessage());
        return R.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }
}
