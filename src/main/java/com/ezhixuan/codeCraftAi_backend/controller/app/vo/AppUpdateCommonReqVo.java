package com.ezhixuan.codeCraftAi_backend.controller.app.vo;

import java.io.Serial;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppUpdateCommonReqVo extends AppConverter implements Serializable {

  @Serial private static final long serialVersionUID = 501343674758930932L;

  @NotNull
  @Schema(description = "应用ID")
  private Long id;

  @Size(min = 1, max = 12, message = "应用名称长度在 1 到 12 个字符之间")
  @Schema(description = "应用名称")
  private String name;
}
