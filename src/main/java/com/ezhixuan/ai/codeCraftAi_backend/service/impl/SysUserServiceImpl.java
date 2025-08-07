package com.ezhixuan.ai.codeCraftAi_backend.service.impl;

import static com.ezhixuan.ai.codeCraftAi_backend.domain.enums.UserRoleEnum.ADMIN;
import static com.ezhixuan.ai.codeCraftAi_backend.domain.enums.UserRoleEnum.getByRole;
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import com.ezhixuan.ai.codeCraftAi_backend.common.PageRequest;
import com.ezhixuan.ai.codeCraftAi_backend.config.prop.SystemProp;
import com.ezhixuan.ai.codeCraftAi_backend.controller.user.vo.*;
import com.ezhixuan.ai.codeCraftAi_backend.domain.constant.UserConstant;
import com.ezhixuan.ai.codeCraftAi_backend.domain.entity.SysUser;
import com.ezhixuan.ai.codeCraftAi_backend.domain.enums.UserRoleEnum;
import com.ezhixuan.ai.codeCraftAi_backend.domain.enums.UserStatusEnum;
import com.ezhixuan.ai.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.ai.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.ai.codeCraftAi_backend.mapper.SysUserMapper;
import com.ezhixuan.ai.codeCraftAi_backend.service.SysUserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 服务层实现。
 *
 * @author Ezhixuan
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SystemProp prop;

    @Override
    public Long doRegister(UserRegisterReqVo reqVo) {
        String account = reqVo.getAccount();
        String password = reqVo.getPassword();
        String confirmPassword = reqVo.getConfirmPassword();
        // 参数校验
        if (!Objects.equals(password, confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不一致");
        }
        if (mapper.selectCountByQuery(QueryWrapper.create().eq(SysUser::getAccount, account)) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        // 密码加密
        String encryptedPassword = getEncryptedPassword(password);
        // 存储数据
        SysUser sysUser = new SysUser();
        sysUser.setPassword(encryptedPassword);
        sysUser.setAccount(account);
        sysUser.setName("用户" + System.currentTimeMillis());
        sysUser.setStatus(UserStatusEnum.NORMAL.getCode());
        sysUser.setRole(count() == 0 ? ADMIN.getRole() : UserRoleEnum.USER.getRole());
        save(sysUser);
        return sysUser.getId();
    }

    @Override
    public UserInfoCommonResVo doLogin(UserLoginReqVo reqVo, HttpServletRequest request) {
        String account = reqVo.getAccount();
        String password = reqVo.getPassword();
        // 获取加密后的密码
        String encryptedPassword = getEncryptedPassword(password);
        // 判断用户是否存在
        SysUser user =
            getOne(QueryWrapper.create().eq(SysUser::getAccount, account).eq(SysUser::getPassword, encryptedPassword));
        if (isNull(user)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        if (Objects.equals(user.getStatus(), UserStatusEnum.DISABLED.getCode())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户已被禁用");
        }
        // 记录用户登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return getUserVo(user);
    }

    @Override
    public UserInfoCommonResVo getUserVo(SysUser user) {
        return BeanUtil.copyProperties(user, UserInfoCommonResVo.class);
    }

    @Override
    public UserInfoCommonResVo getUserVo(HttpServletRequest request) {
        // 判断用户是否已登录
        SysUser user = getUserByHttpReq(request);
        Long id = user.getId();
        if (isNull(id)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        SysUser byId = getById(id);
        if (isNull(byId)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return getUserVo(byId);
    }

    @Override
    public void doLogout(HttpServletRequest request) {
        getUserByHttpReq(request);
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
    }

    @Override
    public List<UserAddResVo> saveBatch(List<UserAddReqVo> waitAddList) {
        if (CollectionUtils.isEmpty(waitAddList)) {
            return Collections.emptyList();
        }
        List<SysUser> list = waitAddList.stream().map(add -> add.toUser(this, prop.getDefaultPassword())).toList();
        saveBatch(list);
        return toAddResVo(list);
    }

    @Override
    public List<UserAddResVo> saveBatch(Integer size) {
        if (size <= 0) {
            return Collections.emptyList();
        }

        if (size > 1000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "批量添加用户数量不能大于1000");
        }

        List<UserAddReqVo> waitAddList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            UserAddReqVo add = new UserAddReqVo();
            waitAddList.add(add);
        }
        return saveBatch(waitAddList);
    }

    @Override
    public void doDisable(Long disableId) {
        SysUser disableUser = mapper.selectOneById(disableId);
        if (isNull(disableUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        if (Objects.equals(getByRole(disableUser.getRole()), ADMIN)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限操作管理员");
        }
        disableUser.setStatus(0);
        updateById(disableUser);
    }

    @Override
    public Page<UserInfoAdminResVo> getList(UserQueryReqVo queryReqVo) {
        return PageRequest.convert(page(queryReqVo.toPage(), getQueryWrapper(queryReqVo)), UserInfoAdminResVo.class);
    }

    private QueryWrapper getQueryWrapper(UserQueryReqVo queryReqVo) {
        return QueryWrapper.create().like(SysUser::getAccount, queryReqVo.getAccount())
            .like(SysUser::getName, queryReqVo.getName()).eq(SysUser::getRole, queryReqVo.getRole())
            .eq(SysUser::getStatus, queryReqVo.getStatus())
            .orderBy(SysUser::getUpdateTime, Objects.equals(queryReqVo.getOrderBy(), PageRequest.ASC));
    }

    private List<UserAddResVo> toAddResVo(List<SysUser> userList) {
        return userList.stream().map(user -> {
            UserAddResVo userAddResVo = new UserAddResVo();
            userAddResVo.setAccount(user.getAccount());
            userAddResVo.setPassword(prop.getDefaultPassword());
            return userAddResVo;
        }).toList();
    }

    /**
     * 通过 httpRequest 获取用户信息
     * @param request 请求
     * @return 用户信息
     */
    private SysUser getUserByHttpReq(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (isNull(userObj)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return (SysUser)userObj;
    }

    /**
     * 加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    public String getEncryptedPassword(String password) {
        return DigestUtils.md5DigestAsHex((password + prop.getSalt()).getBytes());
    }
}
