package com.ezhixuan.codeCraftAi_backend.controller.chatHistory;

import com.ezhixuan.codeCraftAi_backend.annotation.AuthRole;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo.ChatInfoResVo;
import com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo.ChatQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.service.SysChatHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat/history")
@Validated
@Tag(name = "ChatHistoryController", description = "对话历史控制器")
@RequiredArgsConstructor
public class ChatHistoryController {

  private final SysChatHistoryService chatHistoryService;

  @Operation(summary = "获取对话历史列表")
  @GetMapping("/list")
  public PageResponse<ChatInfoResVo> list(@Valid ChatQueryReqVo reqVo) {
    return R.list(chatHistoryService.list(reqVo));
  }

  @Operation(summary = "获取对话历史列表 (管理员)")
  @AuthRole
  @GetMapping("/admin/list")
  public PageResponse<ChatInfoResVo> adminList(ChatQueryReqVo reqVo) {
    return R.list(chatHistoryService.list(reqVo));
  }
}
