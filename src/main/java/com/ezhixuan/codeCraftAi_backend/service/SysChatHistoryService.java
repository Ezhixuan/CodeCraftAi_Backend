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
 * 对话历史服务接口 提供对话历史相关的业务逻辑处理接口，包括保存、查询、删除对话记录等功能
 *
 * @author Ezhixuan
 * @version 0.0.1beta
 */
@Validated
public interface SysChatHistoryService extends IService<SysChatHistory> {

  /**
   * 保存历史对话 将用户与AI的对话记录保存到数据库中
   *
   * @param chatHistorySubmitDto 对话记录提交参数
   * @return boolean 是否保存成功
   */
  boolean save(@Valid SysChatHistorySubmitDto chatHistorySubmitDto);

  /**
   * 根据应用ID删除对话记录 删除指定应用下的所有对话记录
   *
   * @param appId 应用ID
   * @return boolean 是否删除成功
   */
  boolean removeByAppId(@NotNull Long appId);

  /**
   * 滚动列表查询 分页查询对话记录列表，支持滚动加载
   *
   * @param reqVo 查询参数
   * @return Page<ChatInfoResVo> 对话记录分页列表
   */
  Page<ChatInfoResVo> list(ChatQueryReqVo reqVo);

  /**
   * 加载聊天记忆消息 从数据库加载指定记忆ID的聊天记录，用于恢复对话上下文
   *
   * @param memoryId 记忆ID
   * @param maxMessages 最大消息数
   * @return List<SysChatHistory> 聊天记录列表
   */
  List<SysChatHistory> loadChatMemoryMessages(long memoryId, int maxMessages);
}
