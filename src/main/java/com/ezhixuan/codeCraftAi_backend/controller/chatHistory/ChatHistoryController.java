package com.ezhixuan.codeCraftAi_backend.controller.chatHistory;

import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo.ChatInfoResVo;
import com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo.ChatUserQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysChatHistory;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.codeCraftAi_backend.service.SysChatHistoryService;
import com.ezhixuan.codeCraftAi_backend.utils.UserUtil;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/chat/history")
@Validated
@Tag(name = "ChatHistoryController", description = "对话历史控制器")
@RequiredArgsConstructor
public class ChatHistoryController {

  private final SysChatHistoryService chatHistoryService;

  @Operation(summary = "获取对话历史列表")
  @GetMapping("/list")
  public PageResponse<ChatInfoResVo> getChatHisList(@Valid ChatUserQueryReqVo reqVo) {
    Page<ChatInfoResVo> paged = chatHistoryService.list(reqVo.toQueryReqVo());
    paged.setRecords(paged.getRecords().reversed());
    return R.list(paged);
  }

  @Operation(summary = "删除对话记录")
  @DeleteMapping("/{id}")
  public BaseResponse<Void> delChatHis(@PathVariable Long id) {
    SysChatHistory chatHistory = chatHistoryService.getById(id);
    if (Objects.isNull(chatHistory)
        || !UserUtil.isAdmin()
        || UserUtil.isMe(chatHistory.getUserId())) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }
    chatHistoryService.removeById(id);
    return R.success();
  }
}
