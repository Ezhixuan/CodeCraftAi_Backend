package com.ezhixuan.codeCraftAi_backend.annotation;

import com.ezhixuan.codeCraftAi_backend.domain.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限角色注解
 * 用于标记需要特定角色权限才能访问的方法，默认需要管理员权限
 *
 * @author Ezhixuan
 * @version 0.0.1beta
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRole {

    /**
     * 默认即为需要管理员权限
     * @return UserRoleEnum 用户角色
     */
    UserRoleEnum role() default UserRoleEnum.ADMIN;
}
