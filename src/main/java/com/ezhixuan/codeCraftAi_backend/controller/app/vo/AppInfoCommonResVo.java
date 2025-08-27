package com.ezhixuan.codeCraftAi_backend.controller.app.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppInfoCommonResVo extends AppConverter implements Serializable {

  @Serial private static final long serialVersionUID = -863368428809848433L;

  @Schema(description = "应用ID")
  private Long id;

  @Schema(description = "应用名称")
  private String name;

  @Schema(description = "应用封面")
  private String cover;

  @Schema(description = "部署时间")
  private LocalDateTime deployTime;

  @Schema(description = "用户 id")
  private Long userId;

  @Schema(description = " 创建时间")
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;

  @Schema(description = "用户信息")
  private UserInfoCommonResVo userInfo;

  public void build(SysApp sysApp, UserInfoCommonResVo userInfo) {
    super.build(sysApp);
    this.userInfo = userInfo;
  }
}
