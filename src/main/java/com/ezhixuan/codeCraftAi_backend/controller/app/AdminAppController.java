package com.ezhixuan.codeCraftAi_backend.controller.app;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ezhixuan.codeCraftAi_backend.annotation.AuthRole;
import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppUpdateAdminReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.ezhixuan.codeCraftAi_backend.service.SysAppService;
import com.ezhixuan.codeCraftAi_backend.service.SysUserService;
import com.mybatisflex.core.paginate.Page;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/app/admin")
@Validated
@Tag(name = "应用控制器(管理员)")
@RequiredArgsConstructor
public class AdminAppController {

    private final SysAppService appService;
    private final SysUserService userService;

    @Operation(summary = "获取应用详情")
    @AuthRole
    @GetMapping("/{id}")
    public BaseResponse<AppInfoAdminResVo> adminGetInfo(@PathVariable Long id) {
        SysApp sysApp = appService.getById(id);
        AppInfoAdminResVo appInfoAdminResVo = new AppInfoAdminResVo();
        appInfoAdminResVo.build(sysApp, BeanUtil.copyProperties(userService.getById(id), UserInfoAdminResVo.class));
        return R.success(appInfoAdminResVo);
    }

    @Operation(summary = "获取用户应用列表")
    @AuthRole
    @GetMapping("/list")
    public PageResponse<AppInfoAdminResVo> adminGetList(@Valid AppQueryReqVo queryReqVo) {
        Page<SysApp> sysAppPage = appService.getList(queryReqVo, false);
        Set<Long> userIds = sysAppPage.getRecords().stream().map(SysApp::getUserId).collect(Collectors.toSet());
        return R.list(appService.convert2Admin(sysAppPage, userService.getUserInfoAdminMap(userIds)));
    }

    @Operation(summary = "更新应用信息")
    @AuthRole
    @PostMapping("/update")
    public BaseResponse<Void> adminUpdate(@RequestBody @Valid AppUpdateAdminReqVo updateReqVo) {
        SysApp entity = updateReqVo.toEntity();
        appService.updateById(entity);
        return R.success();
    }
}
