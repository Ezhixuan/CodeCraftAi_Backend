package com.ezhixuan.codeCraftAi_backend.controller.app;

import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.mybatisflex.core.paginate.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppGenerateReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.service.SysAppService;
import com.ezhixuan.codeCraftAi_backend.service.SysUserService;

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
        return R.list();
    }

    @Operation(summary = "获取用户应用列表(用户)")
    @GetMapping("/admin/list")
    public PageResponse<AppInfoCommonResVo> adminGetList(@RequestBody AppQueryReqVo queryReqVo) {
        Page<SysApp> sysAppPage = appService.getList(queryReqVo, false);
        return R.list();
    }
}
