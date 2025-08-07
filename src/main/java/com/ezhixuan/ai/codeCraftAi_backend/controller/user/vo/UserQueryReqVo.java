package com.ezhixuan.ai.codeCraftAi_backend.controller.user.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.ezhixuan.ai.codeCraftAi_backend.common.PageRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryReqVo extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1731786451769702901L;

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "用户账号")
    private String account;

    @Schema(description = "用户昵称")
    private String name;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "用户角色")
    private String role;
}
