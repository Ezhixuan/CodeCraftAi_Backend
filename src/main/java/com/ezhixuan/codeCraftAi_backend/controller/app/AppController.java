package com.ezhixuan.codeCraftAi_backend.controller.app;

import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppGenerateReqVo;
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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/app")
@Validated
@Tag(name = "应用控制器")
@RequiredArgsConstructor
public class AppController {

  private final SysAppService appService;
  private final SysUserService userService;

  @Operation(summary = "代码生成")
  @GetMapping(value = "/generate/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<String>> generateCode(
      @RequestParam("message") @NotBlank String message,
      @RequestParam("appId") @NotNull @Min(1) Long appId) {
    return appService.generateCode(message, appId);
  }

  @Operation(summary = "通过用户输入生成应用记录")
  @PostMapping("/generate")
  public BaseResponse<Long> doGenerate(@RequestBody @Valid AppGenerateReqVo reqVo) {
    return R.success(appService.doGenerate(reqVo));
  }

  @Operation(summary = "获取应用详情")
  @GetMapping("/{id}")
  public BaseResponse<AppInfoCommonResVo> getInfo(@PathVariable Long id) {
    SysApp sysApp = appService.getById(id);
    AppInfoCommonResVo appInfoCommonResVo = new AppInfoCommonResVo();
    appInfoCommonResVo.build(sysApp, userService.getUserVo(sysApp.getUserId()));
    return R.success(appInfoCommonResVo);
  }

  @Operation(summary = "应用预览")
  @GetMapping("/preview/{appId}")
  public void doPreview(
      @PathVariable("appId") Long appId, boolean reBuild, HttpServletResponse response) {
    String previewKey = appService.copyToPreview(appId, reBuild);
    appService.redirect(previewKey, response);
  }

  @Operation(summary = "获取用户应用列表")
  @GetMapping("/list")
  public PageResponse<AppInfoCommonResVo> getList(@Valid AppQueryReqVo queryReqVo) {
    Page<SysApp> sysAppPage = appService.getList(queryReqVo, true);
    return R.list(
        appService.convert2Common(
            sysAppPage,
            userService.getUserInfoCommonMap(
                Collections.singletonList(UserUtil.getLoginUserId()))));
  }

  @Operation(summary = "获取精选应用列表")
  @GetMapping("/list/featured")
  public PageResponse<AppInfoCommonResVo> getFeaturedList() {
    AppQueryReqVo appQueryReqVo = new AppQueryReqVo();
    appQueryReqVo.setPriority(1);
    appQueryReqVo.setPageNo(1);
    appQueryReqVo.setPageSize(30);
    Page<SysApp> sysAppPage = appService.getList(appQueryReqVo, false);
    Set<Long> userIds =
        sysAppPage.getRecords().stream().map(SysApp::getUserId).collect(Collectors.toSet());
    return R.list(appService.convert2Common(sysAppPage, userService.getUserInfoCommonMap(userIds)));
  }

  @Operation(summary = "更新应用信息")
  @PostMapping("/update")
  public BaseResponse<Void> update(@RequestBody @Valid AppUpdateCommonReqVo updateReqVo) {
    if (!UserUtil.isMe(updateReqVo.getId())) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }
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
