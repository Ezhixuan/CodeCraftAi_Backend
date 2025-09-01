package com.ezhixuan.codeCraftAi_backend.interceptor;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ezhixuan.codeCraftAi_backend.annotation.AuthRole;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.domain.enums.UserRoleEnum;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.codeCraftAi_backend.service.SysUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * 权限角色拦截器
 * 用于拦截带有@AuthRole注解的方法，验证用户是否具有相应的角色权限
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuthRoleInterceptor {

  private final SysUserService userService;

  /**
   * 拦截处理方法
   * 拦截带有@AuthRole注解的方法，验证用户角色权限
   *
   * @param joinPoint 连接点对象
   * @param authRole 权限角色注解
   * @return 方法执行结果
   * @throws Throwable 方法执行异常
   */
  @Around("@annotation(authRole)")
  @SneakyThrows
  public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthRole authRole) {
    // 获取用户信息
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
    UserInfoCommonResVo userVo = userService.getUserVo(request);
    if (Objects.isNull(userVo)) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
    }
    // 判断权限是否一致
    UserRoleEnum needRoleEnum = authRole.role();
    UserRoleEnum userRoleEnum = UserRoleEnum.getByValue(userVo.getRole());
    if (!Objects.equals(needRoleEnum, userRoleEnum)) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }
    return joinPoint.proceed();
  }
}
