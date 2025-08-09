package com.ezhixuan.codeCraftAi_backend.utils;

import static java.util.Objects.isNull;

import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ezhixuan.codeCraftAi_backend.domain.constant.UserConstant;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysUser;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtil {

    public String SALT = "xuanAi";

    /**
     * 获取当前登录用户 id,如果不存在抛出 NOT_LOGIN
     * @author Ezhixuan
     * @param request 请求
     * @return Long 当前登录用户 id
     */
    public Long getLoginUserId(HttpServletRequest request) {
        SysUser loginUserInfo = getLoginUserInfo(request);
        Long id = loginUserInfo.getId();
        if (isNull(id)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return id;
    }

    public Long getLoginUserId() {
        return getLoginUserId(getRequest());
    }

    /**
     * 获取当前登录用户信息,如果不存在抛出 NOT_LOGIN
     * @author Ezhixuan
     * @param request 请求
     * @return SysUser 当前登录用户信息
     */
    public SysUser getLoginUserInfo(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (isNull(userObj)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return (SysUser)userObj;
    }

    public SysUser getLoginUserInfo() {
        return getLoginUserInfo(getRequest());
    }

    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    /**
     * 密码加密
     * @author Ezhixuan
     * @param password 原密码
     * @return String 加密后的密码
     */
    public String getEncryptedPassword(String password){
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes());
    }
}
