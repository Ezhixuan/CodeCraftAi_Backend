package com.ezhixuan.codeCraftAi_backend.domain.enums;

import lombok.Getter;

@Getter
public enum MessageTypeEnum {
    USER("user", "用户消息"),
    AI("ai", "AI消息"),;

    private final String type;
    private final String desc;

    MessageTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static MessageTypeEnum getByType(String type) {
        for (MessageTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
