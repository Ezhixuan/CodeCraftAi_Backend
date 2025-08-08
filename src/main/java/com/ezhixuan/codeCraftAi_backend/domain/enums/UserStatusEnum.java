package com.ezhixuan.codeCraftAi_backend.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public enum UserStatusEnum {
    NORMAL(1, "正常"), DISABLED(0, "禁用");

    @Schema(description = "状态码")
    private final Integer code;

    @Schema(description = "状态描述")
    private final String message;

    UserStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(Integer code) {
        for (UserStatusEnum value : UserStatusEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getMessage();
            }
        }
        return null;
    }
}
