package com.ezhixuan.codeCraftAi_backend.utils;

import com.ezhixuan.codeCraftAi_backend.domain.constant.UserConstant;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysUser;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

import static java.util.Objects.isNull;

/**
 * 用户工具类 提供用户相关的工具方法，包括获取当前登录用户信息、密码加密、权限判断等功能
 *
 * @author Ezhixuan
 * @version 0.0.1beta
 */
public class UserUtil {

  /** 密码加密盐值 */
  public static String SALT = "xuanAi";

  /**
   * 获取当前登录用户ID 从HTTP请求中获取当前登录用户的ID，如果用户未登录则抛出未登录异常
   *
   * @param request HTTP请求对象
   * @return Long 当前登录用户ID
   * @throws BusinessException 当用户未登录时抛出NOT_LOGIN异常
   */
  public static Long getLoginUserId(HttpServletRequest request) {
    SysUser loginUserInfo = getLoginUserInfo(request);
    Long id = loginUserInfo.getId();
    if (isNull(id)) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
    }
    return id;
  }

  /**
   * 获取当前登录用户ID 通过当前请求上下文获取当前登录用户的ID
   *
   * @return Long 当前登录用户ID
   */
  public static Long getLoginUserId() {
    return getLoginUserId(getRequest());
  }

  /**
   * 获取当前登录用户信息 从HTTP请求中获取当前登录用户的完整信息，如果用户未登录则抛出未登录异常
   *
   * @param request HTTP请求对象
   * @return SysUser 当前登录用户信息
   * @throws BusinessException 当用户未登录时抛出NOT_LOGIN异常
   */
  public static SysUser getLoginUserInfo(HttpServletRequest request) {
    Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
    if (isNull(userObj)) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
    }
    return (SysUser) userObj;
  }

  /**
   * 获取当前登录用户信息 通过当前请求上下文获取当前登录用户的完整信息
   *
   * @return SysUser 当前登录用户信息
   */
  public static SysUser getLoginUserInfo() {
    return getLoginUserInfo(getRequest());
  }

  /**
   * 获取当前HTTP请求对象 从请求上下文持有者中获取当前的HTTP请求对象
   *
   * @return HttpServletRequest 当前HTTP请求对象
   */
  public static HttpServletRequest getRequest() {
    return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
        .getRequest();
  }

  /**
   * 密码加密 使用MD5算法和盐值对用户密码进行加密处理
   *
   * @param password 原始密码
   * @return String 加密后的密码
   */
  public static String getEncryptedPassword(String password) {
    return DigestUtils.md5DigestAsHex((SALT + password).getBytes());
  }

  /**
   * 判断当前用户是否为管理员 检查当前登录用户的角色是否为管理员角色
   *
   * @return boolean true-管理员 false-非管理员
   */
  public static boolean isAdmin() {
    try {
      SysUser loginUserInfo = getLoginUserInfo();
      return UserConstant.ADMIN_ROLE.equals(loginUserInfo.getRole());
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 判断操作者是否为本人 检查指定的操作者ID是否与当前登录用户ID一致
   *
   * @param operateId 操作者ID
   * @return boolean true-本人 false-非本人
   */
  public static boolean isMe(Long operateId) {
    try {
      return Objects.equals(operateId, getLoginUserId());
    } catch (Exception e) {
      return false;
    }
  }
}
