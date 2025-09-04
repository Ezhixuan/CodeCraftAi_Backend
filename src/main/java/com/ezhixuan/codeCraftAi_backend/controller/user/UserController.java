package com.ezhixuan.codeCraftAi_backend.controller.user;

import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserLoginReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserRegisterReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserUpdateReqVo;
import com.ezhixuan.codeCraftAi_backend.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
@Validated
@Tag(name = "UserController", description = "用户控制器")
@RequiredArgsConstructor
public class UserController {

  private final SysUserService userService;

  @Operation(summary = "用户注册")
  @PostMapping("/register")
  public BaseResponse<Long> postUserRegister(@RequestBody @Valid UserRegisterReqVo reqVo) {
    return R.success(userService.doRegister(reqVo));
  }

  @Operation(summary = "用户登录")
  @PostMapping("/login")
  public BaseResponse<UserInfoCommonResVo> postUserLogin(
      @RequestBody @Valid UserLoginReqVo reqVo, HttpServletRequest request) {
    return R.success(userService.doLogin(reqVo, request));
  }

  @Operation(summary = "获取用户信息")
  @GetMapping
  public BaseResponse<UserInfoCommonResVo> getLoginUserInfo(HttpServletRequest request) {
    try {
      return R.success(userService.getUserVo(request));
    } catch (Exception exception) {
      return R.success();
    }
  }

  @Operation(summary = "通过 id 获取用户信息")
  @GetMapping("/{id}")
  public BaseResponse<UserInfoCommonResVo> getUserInfoById(@PathVariable Long id) {
    return R.success(userService.getUserVo(id));
  }

  @Operation(summary = "退出登录")
  @PostMapping("/logout")
  public BaseResponse<Void> postUserLogout(HttpServletRequest request) {
    userService.doLogout(request);
    return R.success();
  }

  @Operation(summary = "修改用户信息")
  @PutMapping
  public BaseResponse<UserInfoCommonResVo> putUserUpdate(
      @Valid @RequestBody UserUpdateReqVo updateReqVo) {
    userService.updateById(updateReqVo.tuEntity());
    return R.success(userService.getUserVo(updateReqVo.getId()));
  }
}
