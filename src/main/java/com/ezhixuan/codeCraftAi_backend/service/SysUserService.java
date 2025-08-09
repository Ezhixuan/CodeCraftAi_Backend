package com.ezhixuan.codeCraftAi_backend.service;

import java.util.List;

import com.ezhixuan.codeCraftAi_backend.controller.user.vo.*;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysUser;
import com.mybatisflex.core.paginate.Page;
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
    UserInfoCommonResVo doLogin(UserLoginReqVo reqVo, HttpServletRequest request);

    /**
     * 获取用户 vo 信息
     * @author Ezhixuan
     * @param user  用户信息
     * @return UserResVo 脱敏后的用户信息
     */
    UserInfoCommonResVo getUserVo(SysUser user);

    /**
     * 获取用户 vo 信息
     * @author Ezhixuan
     * @param request 请求
     * @return UserResVo 脱敏后的用户信息
     */
    UserInfoCommonResVo getUserVo(HttpServletRequest request);

    /**
     * 通过 id 获取用户 vo 信息
     * @author Ezhixuan
     * @param id 用户 Id
     * @return UserInfoCommonResVo 脱敏后的用户信息
     */
    UserInfoCommonResVo getUserVo(Long id);

    /**
     * 用户退出
     * @author Ezhixuan
     * @param request 请求
     */
    void doLogout(HttpServletRequest request);

    /**
     * 批量新增用户
     * @author Ezhixuan
     * @param waitAddList 新增用户列表
     * @return List<UserAddResVo> 新增成功的用户信息
     */
    List<UserAddResVo> saveBatch(List<UserAddReqVo> waitAddList);

    /**
     * 批量创建 size 个用户账号
     * @author Ezhixuan
     * @param size 创建数量
     * @return List<UserAddResVo> 创建成功的用户信息
     */
    List<UserAddResVo> saveBatch(Integer size);

    /**
     * 账号停用
     * @author Ezhixuan
     * @param disableId 停用账号 Id
     */
    void doDisable(Long disableId);

    /**
     * 分页查询用户信息
     * @author Ezhixuan
     * @param queryReqVo 查询参数
     * @return Page<SysUser> 用户信息
     */
    Page<UserInfoAdminResVo> getList(UserQueryReqVo queryReqVo);
}
