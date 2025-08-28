package com.ezhixuan.codeCraftAi_backend.controller.app.vo;

import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AppUpdateAdminReqVo implements Serializable {

  @Serial private static final long serialVersionUID = -8337583786116753335L;

  @NotNull
  @Schema(description = "应用ID")
  private Long id;

  @Size(min = 1, max = 12, message = "应用名称长度在 1 到 12 个字符之间")
  @Schema(description = "应用名称")
  private String name;

  @Schema(description = "应用封面")
  private String cover;

  @Schema(description = "部署密钥")
  private String deployKey;

  @Min(value = 0, message = "应用优先级最小为 0")
  @Schema(description = "优先级")
  private Integer priority;

  public SysApp toEntity() {
    SysApp sysApp = new SysApp();
    sysApp.setId(id);
    sysApp.setName(name);
    sysApp.setCover(cover);
    sysApp.setDeployKey(deployKey);
    sysApp.setPriority(priority);
    return sysApp;
  }
}
