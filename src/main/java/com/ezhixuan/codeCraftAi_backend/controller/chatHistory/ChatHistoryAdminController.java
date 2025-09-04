package com.ezhixuan.codeCraftAi_backend.controller.chatHistory;

import com.ezhixuan.codeCraftAi_backend.annotation.AuthRole;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo.ChatInfoResVo;
import com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo.ChatQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.service.SysChatHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin/chat/history")
@RestController
@RequiredArgsConstructor
@Tag(name = "ChatHistoryAdminController", description = "对话历史控制器 (管理员)")
public class ChatHistoryAdminController {

  private final SysChatHistoryService chatHistoryService;

  @Operation(summary = "获取对话历史列表 (管理员)")
  @AuthRole
  @GetMapping("/list")
  public PageResponse<ChatInfoResVo> getChatHisListAdmin(ChatQueryReqVo reqVo) {
    return R.list(chatHistoryService.list(reqVo));
  }
}
