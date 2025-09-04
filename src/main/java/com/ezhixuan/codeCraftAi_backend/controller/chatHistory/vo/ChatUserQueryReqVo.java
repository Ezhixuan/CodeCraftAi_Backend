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
public class ChatUserQueryReqVo extends PageRequest implements Serializable {

  @Serial private static final long serialVersionUID = -1200923620166421507L;

  @NotNull
  @Schema(description = "应用ID")
  private Long appId;

  @Schema(description = "创建时间")
  private LocalDateTime startTime;

  @Schema(description = "结束时间")
  private LocalDateTime endTime;

  public ChatQueryReqVo toQueryReqVo() {
    ChatQueryReqVo chatQueryReqVo = new ChatQueryReqVo();
    chatQueryReqVo.setAppId(appId);
    chatQueryReqVo.setStartTime(startTime);
    chatQueryReqVo.setEndTime(endTime);
    chatQueryReqVo.setPageNo(getPageNo());
    chatQueryReqVo.setPageSize(getPageSize());
    chatQueryReqVo.setOrderBy(getOrderBy());
    return chatQueryReqVo;
  }
}
