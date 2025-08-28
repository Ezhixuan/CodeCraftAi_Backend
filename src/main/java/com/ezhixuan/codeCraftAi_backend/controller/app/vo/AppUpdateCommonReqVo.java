package com.ezhixuan.codeCraftAi_backend.controller.app.vo;

import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AppUpdateCommonReqVo implements Serializable {

  @Serial private static final long serialVersionUID = 501343674758930932L;

  @NotNull
  @Schema(description = "应用ID")
  private Long id;

  @Size(min = 1, max = 12, message = "应用名称长度在 1 到 12 个字符之间")
  @Schema(description = "应用名称")
  private String name;

  public SysApp toEntity() {
    SysApp sysApp = new SysApp();
    sysApp.setId(id);
    sysApp.setName(name);
    return sysApp;
  }
}
