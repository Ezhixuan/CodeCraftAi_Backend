package com.ezhixuan.ai.codeCraftAi_backend.domain.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    USER("USER", "用户"), ADMIN("ADMIN", "管理员"),;

    private String role;

    private String desc;

    UserRoleEnum(String role, String desc) {
        this.role = role;
        this.desc = desc;
    }

    public static UserRoleEnum getByRole(String role) {
        for (UserRoleEnum value : UserRoleEnum.values()) {
            if (value.getRole().equals(role)) {
                return value;
            }
        }
        return null;
    }
}
