package com.ezhixuan.codeCraftAi_backend.annotation;

import com.ezhixuan.codeCraftAi_backend.domain.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRole {

    /**
     * 默认即为需要管理员权限
     * @author Ezhixuan
     * @return UserRoleEnum 用户角色
     */
    UserRoleEnum role() default UserRoleEnum.ADMIN;
}
