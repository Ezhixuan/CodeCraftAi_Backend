package com.ezhixuan.codeCraftAi_backend.service;

import com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo.ChatInfoResVo;
import com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo.ChatQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.domain.dto.sys.chatHistory.SysChatHistorySubmitDto;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysChatHistory;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 对话历史 服务层。
 *
 * @author Ezhixuan
 */
@Validated
public interface SysChatHistoryService extends IService<SysChatHistory> {

    /**
     * 保存历史对话
     * @author Ezhixuan
     * @param chatHistorySubmitDto 提交参数
     * @return boolean
     */
    boolean save(@Valid SysChatHistorySubmitDto chatHistorySubmitDto);

    /**
     * 根据 appId 删除对话记录
     * @author Ezhixuan
     * @param appId 应用 id
     * @return boolean
     */
    boolean removeByAppId(@NotNull Long appId);

    /**
     * 滚动列表查询
     * @author Ezhixuan
     * @param reqVo 查询参数
     * @return PageResponse<ChatQueryReqVo> 查询结果列表
     */
    Page<ChatInfoResVo> list(ChatQueryReqVo reqVo);

    /**
     * 加载聊天记忆消息
     * @param memoryId 记忆 id
     * @param maxMessages 最大消息数
     * @author Ezhixuan
     * @return List<SysChatHistory> 聊天记录
     */
    List<SysChatHistory> loadChatMemoryMessages(long memoryId, int maxMessages);
}
