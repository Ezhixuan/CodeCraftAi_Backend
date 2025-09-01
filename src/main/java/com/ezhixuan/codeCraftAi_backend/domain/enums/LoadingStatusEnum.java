package com.ezhixuan.codeCraftAi_backend.domain.enums;

import lombok.Getter;

/**
 * 加载状态枚举
 * 定义资源或任务的加载状态，包括加载中、已加载和加载失败三种状态
 *
 * @author ezhixuan
 * @version 0.0.2beta
 */
@Getter
public enum LoadingStatusEnum {
  /** 加载中状态 */
  LOADING(0, "加载中"),
  /** 已加载状态 */
  LOADED(1, "已加载"),
  /** 加载失败状态 */
  ERROR(2, "加载失败");

  /** 状态码 */
  private final int value;

  /** 状态描述信息 */
  private final String desc;

  LoadingStatusEnum(int value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  /**
   * 根据状态码获取枚举值 <br>
   * 遍历枚举值，找到与给定状态码匹配的枚举项
   *
   * @param value 状态码
   * @return 匹配的状态描述信息枚举，如果未找到则返回error
   */
  public static LoadingStatusEnum getByValue(int value) {
    for (LoadingStatusEnum status : LoadingStatusEnum.values()) {
      if (status.value == value) {
        return status;
      }
    }
    return ERROR;
  }
}
