package com.ezhixuan.codeCraftAi_backend.controller.deploy.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "部署状态响应")
public class DeployStatusVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "应用ID")
    private Long appId;

    @Schema(description = "部署标识")
    private String preDeployKey;

    @Schema(description = "部署时间")
    private LocalDateTime deployTime;

    @Schema(description = "临时文件是否存在")
    private Boolean tempFileExists;

    @Schema(description = "部署文件是否存在")
    private Boolean deployFileExists;
}
