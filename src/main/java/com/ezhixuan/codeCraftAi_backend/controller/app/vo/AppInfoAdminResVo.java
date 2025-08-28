package com.ezhixuan.codeCraftAi_backend.controller.app.vo;

import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AppInfoAdminResVo implements Serializable {

  @Serial private static final long serialVersionUID = 5449833456696378220L;

  @Schema(description = "应用ID")
  private Long id;

  @Schema(description = "应用名称")
  private String name;

  @Schema(description = "应用封面")
  private String cover;

  @Schema(description = "初始提示")
  private String initPrompt;

  @Schema(description = "代码生成类型")
  private String codeGenType;

  @Schema(description = "部署密钥")
  private String deployKey;

  @Schema(description = "部署时间")
  private LocalDateTime deployTime;

  @Schema(description = "优先级")
  private Integer priority;

  @Schema(description = "用户 id")
  private Long userId;

  @Schema(description = " 创建时间")
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;

  @Schema(description = "用户信息")
  private UserInfoAdminResVo userInfo;

  public static AppInfoAdminResVo build(SysApp sysApp, UserInfoAdminResVo userInfo) {
    if (sysApp == null) {
      return null;
    }
    AppInfoAdminResVo appInfoAdminResVo = new AppInfoAdminResVo();
    appInfoAdminResVo.setId(sysApp.getId());
    appInfoAdminResVo.setName(sysApp.getName());
    appInfoAdminResVo.setCover(sysApp.getCover());
    appInfoAdminResVo.setInitPrompt(sysApp.getInitPrompt());
    appInfoAdminResVo.setCodeGenType(sysApp.getCodeGenType());
    appInfoAdminResVo.setDeployKey(sysApp.getDeployKey());
    appInfoAdminResVo.setDeployTime(sysApp.getDeployTime());
    appInfoAdminResVo.setPriority(sysApp.getPriority());
    appInfoAdminResVo.setUserId(sysApp.getUserId());
    appInfoAdminResVo.setCreateTime(sysApp.getCreateTime());
    appInfoAdminResVo.setUpdateTime(sysApp.getUpdateTime());
    appInfoAdminResVo.setUserInfo(userInfo);
    return appInfoAdminResVo;
  }
}
