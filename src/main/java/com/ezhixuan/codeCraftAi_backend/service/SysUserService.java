package com.ezhixuan.codeCraftAi_backend.service;

import com.ezhixuan.codeCraftAi_backend.controller.user.vo.*;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysUser;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户服务接口 提供用户相关的业务逻辑处理接口，包括用户注册、登录、查询、管理等功能
 *
 * @author Ezhixuan
 * @version 0.0.1beta
 */
public interface SysUserService extends IService<SysUser> {

  /**
   * 用户注册 处理用户注册请求，创建新的用户账户
   *
   * @param reqVo 注册请求参数
   * @return Long 用户ID
   */
  Long doRegister(UserRegisterReqVo reqVo);

  /**
   * 用户登录 处理用户登录请求，验证用户身份并返回脱敏后的用户信息
   *
   * @param reqVo 登录请求参数
   * @param request HTTP请求对象
   * @return UserInfoCommonResVo 脱敏后的用户信息
   */
  UserInfoCommonResVo doLogin(UserLoginReqVo reqVo, HttpServletRequest request);

  /**
   * 获取用户VO信息 根据用户实体对象获取脱敏后的用户信息
   *
   * @param user 用户实体对象
   * @return UserInfoCommonResVo 脱敏后的用户信息
   */
  UserInfoCommonResVo getUserVo(SysUser user);

  /**
   * 获取用户VO信息 从HTTP请求中获取用户信息并返回脱敏后的用户信息
   *
   * @param request HTTP请求对象
   * @return UserInfoCommonResVo 脱敏后的用户信息
   */
  UserInfoCommonResVo getUserVo(HttpServletRequest request);

  /**
   * 通过ID获取用户VO信息 根据用户ID获取脱敏后的用户信息
   *
   * @param id 用户ID
   * @return UserInfoCommonResVo 脱敏后的用户信息
   */
  UserInfoCommonResVo getUserVo(Long id);

  /**
   * 用户退出 处理用户退出登录请求，清理用户会话信息
   *
   * @param request HTTP请求对象
   */
  void doLogout(HttpServletRequest request);

  /**
   * 批量新增用户 批量创建用户账户
   *
   * @param waitAddList 待新增的用户列表
   * @return List<UserAddResVo> 新增成功的用户信息列表
   */
  List<UserAddResVo> saveBatch(List<UserAddReqVo> waitAddList);

  /**
   * 批量创建指定数量的用户账号 自动生成并创建指定数量的用户账户
   *
   * @param size 创建用户数量
   * @return List<UserAddResVo> 创建成功的用户信息列表
   */
  List<UserAddResVo> saveBatch(Integer size);

  /**
   * 账号停用 停用指定ID的用户账户
   *
   * @param disableId 待停用的账号ID
   */
  void doDisable(Long disableId);

  /**
   * 分页查询用户信息 根据查询条件分页获取用户列表信息
   *
   * @param queryReqVo 查询参数
   * @return Page<UserInfoAdminResVo> 用户信息分页列表
   */
  Page<UserInfoAdminResVo> getList(UserQueryReqVo queryReqVo);

  /**
   * 根据用户ID列表获取用户通用信息Map 批量获取用户脱敏后的通用信息，并以ID为键构建映射表
   *
   * @param userIdList 用户ID列表
   * @return Map<Long, UserInfoCommonResVo> 用户信息映射表
   */
  Map<Long, UserInfoCommonResVo> getUserInfoCommonMap(Collection<Long> userIdList);

  /**
   * 根据用户ID列表获取用户管理信息Map 批量获取用户脱敏后的管理信息，并以ID为键构建映射表
   *
   * @param userIdList 用户ID列表
   * @return Map<Long, UserInfoAdminResVo> 用户信息映射表
   */
  Map<Long, UserInfoAdminResVo> getUserInfoAdminMap(Collection<Long> userIdList);
}
