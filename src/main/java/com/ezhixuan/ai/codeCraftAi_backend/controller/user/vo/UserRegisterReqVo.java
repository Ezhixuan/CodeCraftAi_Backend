package com.ezhixuan.ai.codeCraftAi_backend.controller.user.vo;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "用户注册请求参数")
public class UserRegisterReqVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2904532590291732640L;

    @Schema(description = "用户账号")
    @NotBlank
    @Length(min = 4, max = 20, message = "账号长度在4-20个字符之间")
    private String account;

    @Schema(description = "密码")
    @NotBlank
    @Length(min = 6, message = "密码长度过短")
    private String password;

    @Schema(description = "核对密码")
    @NotBlank
    @Length(min = 6, message = "密码长度过短")
    private String confirmPassword;
}
