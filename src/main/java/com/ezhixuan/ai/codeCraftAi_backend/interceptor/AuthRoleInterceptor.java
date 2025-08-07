package com.ezhixuan.ai.codeCraftAi_backend.interceptor;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ezhixuan.ai.codeCraftAi_backend.annotation.AuthRole;
import com.ezhixuan.ai.codeCraftAi_backend.controller.user.vo.UserResVo;
import com.ezhixuan.ai.codeCraftAi_backend.domain.enums.UserRoleEnum;
import com.ezhixuan.ai.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.ai.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.ai.codeCraftAi_backend.service.SysUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthRoleInterceptor {

    private final SysUserService userService;

    @Around("@annotation(authRole)")
    @SneakyThrows
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthRole authRole) {
        // 判断是否需要权限
        String needRole = authRole.role();
        if (!StringUtils.hasText(needRole)) {
            joinPoint.proceed();
        }
        // 获取用户信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        UserResVo userVo = userService.getUserVo(request);
        if (Objects.isNull(userVo)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 判断权限是否一致
        UserRoleEnum needRoleEnum = UserRoleEnum.getByRole(needRole);
        UserRoleEnum userRoleEnum = UserRoleEnum.getByRole(userVo.getRole());
        if (!Objects.equals(needRoleEnum, userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}
