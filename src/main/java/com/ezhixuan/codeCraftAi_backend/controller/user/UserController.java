package com.ezhixuan.codeCraftAi_backend.controller.user;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ezhixuan.codeCraftAi_backend.annotation.AuthRole;
import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.*;
import com.ezhixuan.codeCraftAi_backend.service.SysUserService;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@RestController
@Validated
@Tag(name = "用户控制器")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public BaseResponse<Long> doRegister(@RequestBody @Valid UserRegisterReqVo reqVo) {
        return R.success(userService.doRegister(reqVo));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public BaseResponse<UserInfoCommonResVo> doLogin(@RequestBody @Valid UserLoginReqVo reqVo, HttpServletRequest request) {
        return R.success(userService.doLogin(reqVo, request));
    }

    @Operation(summary = "获取用户信息")
    @GetMapping
    public BaseResponse<UserInfoCommonResVo> getUserInfo(HttpServletRequest request) {
        return R.success(userService.getUserVo(request));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public BaseResponse<Void> doLogout(HttpServletRequest request) {
        userService.doLogout(request);
        return R.success();
    }

    @Operation(summary = "修改用户信息")
    @PostMapping
    public BaseResponse<UserInfoCommonResVo> updateUserInfo(@Valid @RequestBody UserUpdateReqVo updateReqVo) {
        userService.updateById(updateReqVo.toUser(userService));
        return R.success(userService.getUserVo(updateReqVo.getId()));
    }

    @Operation(summary = "新增用户(支持批量)")
    @AuthRole
    @PutMapping("/add")
    public PageResponse<UserAddResVo> adminAddByAccount(@RequestBody List<UserAddReqVo> waitAddList) {
        return R.list(userService.saveBatch(waitAddList));
    }

    @Operation(summary = "新增用户(支持批量)")
    @AuthRole
    @PutMapping("/add/{size}")
    public PageResponse<UserAddResVo> adminAddBySize(@PathVariable Integer size) {
        return R.list(userService.saveBatch(size));
    }

    @Operation(summary = "停用账号")
    @AuthRole
    @PutMapping("/disable/{disableId}")
    public BaseResponse<Void> adminDisable(@PathVariable Long disableId) {
        userService.doDisable(disableId);
        return R.success();
    }

    @Operation(summary = "获取用户信息(完整)")
    @AuthRole
    @GetMapping("/{id}/admin")
    public BaseResponse<UserInfoAdminResVo> adminGetUserInfo(@PathVariable Long id) {
        return R.success(BeanUtil.copyProperties(userService.getById(id), UserInfoAdminResVo.class));
    }

    @Operation(summary = "获取用户列表")
    @AuthRole
    @GetMapping("/list")
    public PageResponse<UserInfoAdminResVo> adminGetList(@Valid UserQueryReqVo queryReqVo) {
        return R.list(userService.getList(queryReqVo));
    }
}
