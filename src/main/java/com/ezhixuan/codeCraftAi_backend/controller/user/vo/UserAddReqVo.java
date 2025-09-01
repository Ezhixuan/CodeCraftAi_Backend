package com.ezhixuan.codeCraftAi_backend.controller.user.vo;

import cn.hutool.core.util.RandomUtil;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysUser;
import com.ezhixuan.codeCraftAi_backend.domain.enums.UserRoleEnum;
import com.ezhixuan.codeCraftAi_backend.utils.UserUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

import static org.springframework.util.StringUtils.hasText;

@Data
public class UserAddReqVo implements Serializable {

  @Serial private static final long serialVersionUID = 861502865980429085L;

  @Schema(description = "用户账号")
  private String account;

  @Schema(description = "用户昵称")
  private String name;

  public SysUser toEntity(String defaultPassword) {
    if (!hasText(account)) {
      account = RandomUtil.randomString(10);
    }
    if (!hasText(name)) {
      name = "用户" + account;
    }
    SysUser user = new SysUser();
    user.setAccount(account);
    user.setName(name);
    user.setRole(UserRoleEnum.USER.getValue());
    user.setStatus(1);
    user.setPassword(UserUtil.getEncryptedPassword(defaultPassword));
    return user;
  }
}
