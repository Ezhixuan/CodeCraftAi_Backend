package com.ezhixuan.codeCraftAi_backend.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 用户角色枚举
 * 定义系统中的用户角色类型，包括普通用户和管理员
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Getter
public enum UserRoleEnum {
  /** 普通用户角色 */
  USER("USER", "用户"),
  /** 管理员角色 */
  ADMIN("ADMIN", "管理员"),
  ;

  @Schema(description = "用户角色")
  private final String value;

  @Schema(description = "用户角色描述")
  private final String desc;

  UserRoleEnum(String value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  /**
   * 根据角色标识获取用户角色枚举 <br>
   * 遍历所有枚举值，找到与给定角色标识匹配的枚举项
   *
   * @param value 用户角色标识
   * @return 匹配的用户角色枚举，如果未找到则返回user
   */
  public static UserRoleEnum getByValue(String value) {
    for (UserRoleEnum role : UserRoleEnum.values()) {
      if (role.getValue().equals(value)) {
        return role;
      }
    }
    return USER;
  }
}
