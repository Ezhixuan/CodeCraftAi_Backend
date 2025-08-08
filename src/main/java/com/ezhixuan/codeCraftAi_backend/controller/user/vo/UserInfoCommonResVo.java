package com.ezhixuan.codeCraftAi_backend.controller.user.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserInfoCommonResVo implements Serializable {

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "用户账号")
    private String account;

    @Schema(description = "用户昵称")
    private String name;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户简介")
    private String profile;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "用户角色")
    private String role;
}
