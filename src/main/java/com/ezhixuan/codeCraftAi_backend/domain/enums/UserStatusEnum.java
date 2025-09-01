package com.ezhixuan.codeCraftAi_backend.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 用户状态枚举
 * 定义用户账户的状态，包括正常和禁用两种状态
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Getter
public enum UserStatusEnum {
  /** 正常状态 */
  NORMAL(1, "正常"),
  /** 禁用状态 */
  DISABLED(0, "禁用");

  @Schema(description = "状态码")
  private final Integer value;

  @Schema(description = "状态描述")
  private final String desc;

  UserStatusEnum(Integer value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  /**
   * 根据状态码获取状态描述 遍历所有枚举值，找到与给定状态码匹配的枚举项并返回其描述信息
   *
   * @param code 状态码
   * @return 匹配的状态描述，如果未找到则返回null
   */
  public static String getDesc(Integer code) {
    for (UserStatusEnum value : UserStatusEnum.values()) {
      if (value.getValue().equals(code)) {
        return value.getDesc();
      }
    }
    return DISABLED.getDesc();
  }
}
