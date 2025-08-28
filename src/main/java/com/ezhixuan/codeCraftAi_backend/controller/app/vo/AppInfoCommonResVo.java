package com.ezhixuan.codeCraftAi_backend.controller.app.vo;

import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AppInfoCommonResVo implements Serializable {

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

  public static AppInfoCommonResVo build(SysApp sysApp, UserInfoCommonResVo userInfo) {
    if (sysApp == null) {
      return null;
    }
    AppInfoCommonResVo appInfoCommonResVo = new AppInfoCommonResVo();
    appInfoCommonResVo.setId(sysApp.getId());
    appInfoCommonResVo.setName(sysApp.getName());
    appInfoCommonResVo.setCover(sysApp.getCover());
    appInfoCommonResVo.setDeployTime(sysApp.getDeployTime());
    appInfoCommonResVo.setUserId(sysApp.getUserId());
    appInfoCommonResVo.setCreateTime(sysApp.getCreateTime());
    appInfoCommonResVo.setUpdateTime(sysApp.getUpdateTime());
    appInfoCommonResVo.setUserInfo(userInfo);
    return appInfoCommonResVo;
  }
}
