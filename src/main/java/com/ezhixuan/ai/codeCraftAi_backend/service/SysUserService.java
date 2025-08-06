package com.ezhixuan.ai.codeCraftAi_backend.service;

import com.ezhixuan.ai.codeCraftAi_backend.controller.user.vo.UserLoginReqVo;
import com.ezhixuan.ai.codeCraftAi_backend.controller.user.vo.UserRegisterReqVo;
import com.ezhixuan.ai.codeCraftAi_backend.controller.user.vo.UserResVo;
import com.ezhixuan.ai.codeCraftAi_backend.domain.entity.SysUser;
import com.mybatisflex.core.service.IService;

import jakarta.servlet.http.HttpServletRequest;

/**
 *  服务层。
 *
 * @author Ezhixuan
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户注册
     * @author Ezhixuan
     * @param reqVo 注册请求
     * @return Long 用户 Id
     */
    Long doRegister(UserRegisterReqVo reqVo);

    /**
     * 用户登录
     * @author Ezhixuan
     * @param reqVo 登录请求
     * @param request 请求
     * @return UserResVo 脱敏后的用户信息
     */
    UserResVo doLogin(UserLoginReqVo reqVo, HttpServletRequest request);

    /**
     * 获取用户 vo 信息
     * @author Ezhixuan
     * @param user  用户信息
     * @return UserResVo 脱敏后的用户信息
     */
    UserResVo getUserVo(SysUser user);

    /**
     * 获取用户 vo 信息
     * @author Ezhixuan
     * @param request 请求
     * @return UserResVo 脱敏后的用户信息
     */
    UserResVo getUserVo(HttpServletRequest request);

    /**
     * 用户退出
     * @author Ezhixuan
     * @param request 请求
     */
    void doLogout(HttpServletRequest request);
}
