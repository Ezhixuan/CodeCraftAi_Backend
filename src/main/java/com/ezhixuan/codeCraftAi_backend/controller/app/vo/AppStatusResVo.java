package com.ezhixuan.codeCraftAi_backend.controller.app.vo;

import com.ezhixuan.codeCraftAi_backend.domain.enums.LoadingStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AppStatusResVo implements Serializable {

  @Serial private static final long serialVersionUID = 3974581006307462760L;

  @Schema(description = "预览状态")
  private LoadingStatusEnum previewStatus;

  @Schema(description = "部署状态")
  private LoadingStatusEnum deployStatus;

  @Schema(description = "原始目录状态")
  private LoadingStatusEnum originalDirStatus;
}
