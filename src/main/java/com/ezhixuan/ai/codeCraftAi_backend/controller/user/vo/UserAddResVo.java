package com.ezhixuan.ai.codeCraftAi_backend.controller.user.vo;

import java.io.Serial;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserAddResVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -8848104698629632258L;

    @Schema(description = "用户账户")
    private String account;

    @Schema(description = "密码")
    private String password = "123456";
}
