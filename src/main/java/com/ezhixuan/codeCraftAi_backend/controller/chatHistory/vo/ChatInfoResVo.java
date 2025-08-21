package com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo;

import com.ezhixuan.codeCraftAi_backend.domain.entity.SysChatHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ChatInfoResVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 173287140325228414L;

    @Schema(description = "对话 id")
    private Long id;

    @Schema(description = "对话内容")
    private String message;

    @Schema(description = "对话类型")
    private String messageType;

    @Schema(description = "应用 id")
    private Long appId;

    @Schema(description = "用户 id")
    private Long userId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public ChatInfoResVo(SysChatHistory sysChatHistory) {
        this.id = sysChatHistory.getId();
        this.message = sysChatHistory.getMessage();
        this.messageType = sysChatHistory.getMessageType();
        this.appId = sysChatHistory.getAppId();
        this.userId = sysChatHistory.getUserId();
        this.createTime = sysChatHistory.getCreateTime();
    }
}
