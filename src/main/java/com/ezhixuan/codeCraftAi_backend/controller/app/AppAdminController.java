package com.ezhixuan.codeCraftAi_backend.controller.app;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/admin/app")
@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "AdminAppController", description = "应用控制器(管理员)")
public class AppAdminController {

  private final SysAppService appService;
  private final SysUserService userService;

  @Operation(summary = "获取应用详情")
  @AuthRole
  @GetMapping("/{id}")
  public BaseResponse<AppInfoAdminResVo> getAppInfoAdmin(@PathVariable Long id) {
    SysApp sysApp = appService.getById(id);
    return R.success(
        AppInfoAdminResVo.build(
            sysApp, UserInfoAdminResVo.build(userService.getById(sysApp.getUserId()))));
  }

  @Operation(summary = "获取用户应用列表")
  @AuthRole
  @GetMapping("/list")
  public PageResponse<AppInfoAdminResVo> getAppListAdmin(@Valid AppQueryReqVo queryReqVo) {
    Page<SysApp> sysAppPage = appService.getList(queryReqVo, false);
    Set<Long> userIds =
        sysAppPage.getRecords().stream().map(SysApp::getUserId).collect(Collectors.toSet());
    return R.list(appService.convert2Admin(sysAppPage, userService.getUserInfoAdminMap(userIds)));
  }

  @Operation(summary = "更新应用信息")
  @AuthRole
  @PutMapping("/update")
  public BaseResponse<Void> putAppUpdateAdmin(@RequestBody @Valid AppUpdateAdminReqVo updateReqVo) {
    SysApp entity = updateReqVo.toEntity();
    appService.updateById(entity);
    return R.success();
  }
}
