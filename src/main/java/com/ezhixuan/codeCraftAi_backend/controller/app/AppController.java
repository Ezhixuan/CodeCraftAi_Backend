package com.ezhixuan.codeCraftAi_backend.controller.app;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ezhixuan.codeCraftAi_backend.annotation.AuthRole;
import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.*;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.codeCraftAi_backend.service.SysAppService;
import com.ezhixuan.codeCraftAi_backend.service.SysUserService;
import com.ezhixuan.codeCraftAi_backend.utils.UserUtil;
import com.mybatisflex.core.paginate.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/app")
@Validated
@Tag(name = "应用控制器")
@RequiredArgsConstructor
public class AppController {

    private final SysAppService appService;
    private final SysUserService userService;

    @Operation(summary = "通过用户输入生成应用记录")
    @PostMapping("/generate")
    public BaseResponse<Long> doGenerate(@RequestBody @Valid AppGenerateReqVo reqVo) {
        return R.success(appService.doGenerate(reqVo));
    }

    @Operation(summary = "获取用户应用列表(用户)")
    @GetMapping("/list")
    public PageResponse<AppInfoCommonResVo> getList(@RequestBody AppQueryReqVo queryReqVo) {
        Page<SysApp> sysAppPage = appService.getList(queryReqVo, true);
        Set<Long> userIds = sysAppPage.getRecords().stream().map(SysApp::getUserId).collect(Collectors.toSet());
        return R.list(appService.convert2Common(sysAppPage, userService.getUserInfoCommonMap(userIds)));
    }

    @Operation(summary = "获取用户应用列表(管理员)")
    @AuthRole
    @GetMapping("/admin/list")
    public PageResponse<AppInfoAdminResVo> adminGetList(@RequestBody AppQueryReqVo queryReqVo) {
        Page<SysApp> sysAppPage = appService.getList(queryReqVo, false);
        Set<Long> userIds = sysAppPage.getRecords().stream().map(SysApp::getUserId).collect(Collectors.toSet());
        return R.list(appService.convert2Admin(sysAppPage, userService.getUserInfoAdminMap(userIds)));
    }

    @Operation(summary = "更新应用信息(用户)")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody @Valid AppUpdateCommonReqVo updateReqVo) {
        if (!UserUtil.isMe(updateReqVo.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        SysApp entity = updateReqVo.toEntity();
        appService.updateById(entity);
        return R.success();
    }

    @Operation(summary = "更新应用信息(管理员)")
    @AuthRole
    @PostMapping("/admin/update")
    public BaseResponse<Void> adminUpdate(@RequestBody @Valid AppUpdateAdminReqVo updateReqVo) {
        SysApp entity = updateReqVo.toEntity();
        appService.updateById(entity);
        return R.success();
    }

    @Operation(summary = "删除应用")
    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable Long id) {
        if (!UserUtil.isMe(id)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        appService.removeById(id);
        return R.success();
    }
}
