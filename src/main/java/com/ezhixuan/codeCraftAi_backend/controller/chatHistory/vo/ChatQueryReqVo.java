package com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo;

import com.ezhixuan.codeCraftAi_backend.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatQueryReqVo extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -6028462213233147188L;

    @NotNull
    @Schema(description = "应用ID")
    private Long appId;

    @Schema(description = "用户 id")
    private Long userId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

