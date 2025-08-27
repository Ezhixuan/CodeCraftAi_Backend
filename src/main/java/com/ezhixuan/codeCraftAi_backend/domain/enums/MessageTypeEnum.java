package com.ezhixuan.codeCraftAi_backend.domain.enums;

import lombok.Getter;

/**
 * 消息类型枚举
 * 定义聊天消息的类型，包括用户消息和AI消息
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Getter
public enum MessageTypeEnum {
  /** 用户消息类型 */
  USER("user", "用户消息"),
  /** AI消息类型 */
  AI("ai", "AI消息"),
  ;

  /** 消息类型标识 */
  private final String type;
  /** 消息类型描述 */
  private final String desc;

  MessageTypeEnum(String type, String desc) {
    this.type = type;
    this.desc = desc;
  }

  /**
   * 根据类型获取消息类型枚举
   * 遍历所有枚举值，找到与给定类型匹配的枚举项
   *
   * @param type 消息类型标识
   * @return 匹配的消息类型枚举，如果未找到则返回null
   */
  public static MessageTypeEnum getByType(String type) {
    for (MessageTypeEnum value : values()) {
      if (value.getType().equals(type)) {
        return value;
      }
    }
    return null;
  }
}
