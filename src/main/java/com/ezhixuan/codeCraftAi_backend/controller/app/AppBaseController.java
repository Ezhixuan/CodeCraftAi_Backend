package com.ezhixuan.codeCraftAi_backend.controller.app;

import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.PageRequest;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppUpdateCommonReqVo;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/app")
@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "AppController", description = "应用控制器")
public class AppBaseController {

  private final SysAppService appService;
  private final SysUserService userService;

  @Operation(summary = "获取应用详情")
  @GetMapping("/{id}")
  public BaseResponse<AppInfoCommonResVo> getAppInfo(@PathVariable Long id) {
    SysApp sysApp = appService.getById(id);
    return R.success(AppInfoCommonResVo.build(sysApp, userService.getUserVo(sysApp.getUserId())));
  }

  @Operation(summary = "获取用户应用列表")
  @GetMapping("/list")
  public PageResponse<AppInfoCommonResVo> getAppList(@Valid AppQueryReqVo queryReqVo) {
    Page<SysApp> sysAppPage = appService.getList(queryReqVo, true);
    return R.list(
        appService.convert2Common(
            sysAppPage,
            userService.getUserInfoCommonMap(
                Collections.singletonList(UserUtil.getLoginUserId()))));
  }

  @Operation(summary = "获取精选应用列表")
  @GetMapping("/list/featured")
  public PageResponse<AppInfoCommonResVo> getAppFeaturedList() {
    AppQueryReqVo appQueryReqVo = new AppQueryReqVo();
    appQueryReqVo.setPriority(1);
    appQueryReqVo.setPageNo(1);
    appQueryReqVo.setPageSize(30);
    appQueryReqVo.setOrderBy(PageRequest.DESC);
    Page<SysApp> sysAppPage = appService.getList(appQueryReqVo, false);
    Set<Long> userIds =
        sysAppPage.getRecords().stream().map(SysApp::getUserId).collect(Collectors.toSet());
    return R.list(appService.convert2Common(sysAppPage, userService.getUserInfoCommonMap(userIds)));
  }

  @Operation(summary = "更新应用信息")
  @PutMapping("/update")
  public BaseResponse<Void> putAppUpdate(@RequestBody @Valid AppUpdateCommonReqVo updateReqVo) {
    SysApp sysApp = appService.getById(updateReqVo.getId());
    if (Objects.isNull(sysApp) || !UserUtil.isAdmin() || !UserUtil.isMe(sysApp.getUserId())) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }
    SysApp entity = updateReqVo.toEntity();
    appService.updateById(entity);
    return R.success();
  }

  @Operation(summary = "删除应用")
  @DeleteMapping("/{id}")
  public BaseResponse<Void> delApp(@PathVariable Long id) {
    SysApp sysApp = appService.getById(id);
    if (Objects.isNull(sysApp) || !UserUtil.isAdmin() || !UserUtil.isMe(sysApp.getUserId())) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }
    appService.removeById(id);
    return R.success();
  }
}
