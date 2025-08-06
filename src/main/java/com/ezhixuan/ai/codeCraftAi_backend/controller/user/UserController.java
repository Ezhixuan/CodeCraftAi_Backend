package com.ezhixuan.ai.codeCraftAi_backend.controller.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ezhixuan.ai.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.ai.codeCraftAi_backend.common.R;
import com.ezhixuan.ai.codeCraftAi_backend.controller.user.vo.UserLoginReqVo;
import com.ezhixuan.ai.codeCraftAi_backend.controller.user.vo.UserRegisterReqVo;
import com.ezhixuan.ai.codeCraftAi_backend.controller.user.vo.UserResVo;
import com.ezhixuan.ai.codeCraftAi_backend.service.SysUserService;

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
    public BaseResponse<UserResVo> doLogin(@RequestBody @Valid UserLoginReqVo reqVo, HttpServletRequest request) {
        return R.success(userService.doLogin(reqVo, request));
    }

    @Operation(summary = "获取用户信息")
    @GetMapping
    public BaseResponse<UserResVo> getUserInfo(HttpServletRequest request) {
        return R.success(userService.getUserVo(request));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request) {
        userService.doLogout(request);
        return R.success();
    }
}
