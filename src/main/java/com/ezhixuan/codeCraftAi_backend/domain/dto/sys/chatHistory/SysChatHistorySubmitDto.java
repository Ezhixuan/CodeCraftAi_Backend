package com.ezhixuan.codeCraftAi_backend.domain.dto.sys.chatHistory;

import com.ezhixuan.codeCraftAi_backend.domain.entity.SysChatHistory;
import com.ezhixuan.codeCraftAi_backend.domain.enums.MessageTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SysChatHistorySubmitDto {

  @NotBlank
  @Schema(description = "消息内容")
  private String message;

  @NotNull
  @Schema(description = "消息类型")
  private MessageTypeEnum messageTypeEnum;

  @NotNull
  @Schema(description = "应用 id")
  private Long appId;

  @NotNull
  @Schema(description = "用户 id")
  private Long userId;

  public SysChatHistory toEntity() {
    SysChatHistory entity = new SysChatHistory();
    entity.setMessage(message);
    entity.setMessageType(messageTypeEnum.getValue());
    entity.setAppId(appId);
    entity.setUserId(userId);
    return entity;
  }
}
