package com.ezhixuan.codeCraftAi_backend.controller.deploy;

import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.deploy.vo.DeployStatusVo;
import com.ezhixuan.codeCraftAi_backend.service.IDeployService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deploy")
@Validated
@Tag(name = "静态资源部署控制器")
@RequiredArgsConstructor
@Slf4j
public class DeployController {

    private final IDeployService deployService;

    @Operation(summary = "部署静态资源")
    @GetMapping("/preview")
    public BaseResponse<String> deployPreview(@RequestParam("appId") @NotNull @Min(1) Long appId) {
        return R.success(deployService.preDeploy(appId));
    }

    @Operation(summary = "通过部署标识重定向到静态资源")
    @GetMapping("/redirect/{deployKey}")
    public void redirectToStaticResource(@PathVariable("deployKey") @NotNull String deployKey,
                                        HttpServletResponse response) {
        deployService.redirectToStaticResource(deployKey, response);
    }

    @Operation(summary = "获取应用部署状态")
    @GetMapping("/status")
    public BaseResponse<DeployStatusVo> getDeployStatus(@RequestParam("appId") @NotNull @Min(1) Long appId) {
        return R.success(deployService.getDeployStatus(appId));
    }
}
