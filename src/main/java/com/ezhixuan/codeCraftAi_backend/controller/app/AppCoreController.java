package com.ezhixuan.codeCraftAi_backend.controller.app;

import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppGenerateReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppStatusResVo;
import com.ezhixuan.codeCraftAi_backend.service.SysAppService;
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

@RequestMapping("/app")
@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "AppCoreController", description = "应用核心控制器")
public class AppCoreController {

  private final SysAppService appService;

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

  @Operation(summary = "应用预览")
  @GetMapping("/preview/{appId}")
  public BaseResponse<String> doPreview(@PathVariable("appId") Long appId, boolean reBuild) {
    String previewKey = appService.copyToPreview(appId, reBuild);
    return R.success(appService.getUrl(previewKey, true));
  }

  @Operation(summary = "应用下载")
  @GetMapping("/download/{appId}")
  public void doDownload(@PathVariable("appId") Long appId, HttpServletResponse response) {
    appService.doZip(appId, response);
  }

  @Operation(summary = "应用部署")
  @PutMapping("/deploy/{appId}")
  public BaseResponse<String> doDeploy(@PathVariable("appId") Long appId) {
    return R.success(appService.doDeploy(appId));
  }

  @Operation(summary = "获取应用状态")
  @GetMapping("/status/{appId}")
  public BaseResponse<AppStatusResVo> getStatus(@PathVariable("appId") Long id) {
    return R.success(appService.getStatus(id));
  }
}
