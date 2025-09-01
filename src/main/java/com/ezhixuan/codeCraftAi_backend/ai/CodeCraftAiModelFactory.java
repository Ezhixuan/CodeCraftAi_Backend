package com.ezhixuan.codeCraftAi_backend.ai;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.ai.tools.FileTool;
import com.ezhixuan.codeCraftAi_backend.ai.tools.UserMessageTool;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysChatHistory;
import com.ezhixuan.codeCraftAi_backend.domain.enums.MessageTypeEnum;
import com.ezhixuan.codeCraftAi_backend.service.SysChatHistoryService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

/**
 * AI模型工厂类 负责创建和管理AI服务实例，根据不同的代码生成类型提供相应的AI服务 使用缓存机制提高性能，避免重复创建AI服务实例
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class CodeCraftAiModelFactory {

  /** 最大消息数量，用于限制聊天历史记录的长度 */
  private static final int MAX_MESSAGES = 25;

  private final ChatModel openAiChatModel;
  private final ChatModel routerChatModel;
  private final StreamingChatModel openAiStreamingChatModel;
  private final StreamingChatModel powerfulStreamingChatModel;
  private final RedisChatMemoryStore redisChatMemoryStore;
  private final SysChatHistoryService chatHistoryService;

  private final FileTool fileTool;
  private final UserMessageTool userMessageTool;

  /** AI服务缓存 使用Caffeine缓存AI服务实例，提高性能并减少重复创建的开销 缓存最大容量为100，访问后10分钟过期，写入后30分钟过期 */
  private final LoadingCache<@NonNull Long, CodeCraftAiChatService> AI_SERVICE_CACHE =
      Caffeine.newBuilder()
          .maximumSize(100)
          .expireAfterAccess(Duration.ofMinutes(10))
          .expireAfterWrite(Duration.ofMinutes(30))
          .removalListener(
              (key, value, cause) ->
                  log.debug("缓存 AI 服务被移除，key: {}, value: {}, cause: {}", key, value, cause))
          .build(this::generateAiService);

  /**
   * 获取AI服务实例 使用默认的代码生成类型(HTML_MULTI_FILE)获取AI服务
   *
   * @param memoryId 内存ID，用于标识和获取特定的AI服务实例
   * @return AI服务实例
   */
  public CodeCraftAiChatService getAiService(long memoryId) {
    return getAiService(memoryId, CodeGenTypeEnum.HTML_MULTI_FILE);
  }

  /**
   * 获取AI服务实例 根据代码生成类型获取相应的AI服务实例
   *
   * @param memoryId 内存ID，用于标识和获取特定的AI服务实例
   * @param codeGenTypeEnum 代码生成类型枚举
   * @return AI服务实例
   */
  public CodeCraftAiChatService getAiService(long memoryId, CodeGenTypeEnum codeGenTypeEnum) {
    return switch (codeGenTypeEnum) {
      case HTML, HTML_MULTI_FILE -> AI_SERVICE_CACHE.get(memoryId);
      case VUE_PROJECT ->
          AI_SERVICE_CACHE.get(
              memoryId,
              key ->
                  preGenerateAiService(openAiChatModel, powerfulStreamingChatModel)
                      .chatMemoryProvider(chatMemoryId -> generateChatHistory(memoryId))
                      .tools(fileTool, userMessageTool)
                      .hallucinatedToolNameStrategy(
                          toolExecutionRequest ->
                              ToolExecutionResultMessage.from(
                                  toolExecutionRequest,
                                  "错误：没有名为 " + toolExecutionRequest.name() + " 的工具"))
                      .build());
    };
  }

  public CodeCraftAiChatService getRouterAiService() {
    return AiServices.builder(CodeCraftAiChatService.class).chatModel(routerChatModel).build();
  }

  /**
   * 生成AI服务实例 根据内存ID创建新的AI服务实例
   *
   * @param memoryId 内存ID，用于生成特定的聊天历史记录
   * @return 新创建的AI服务实例
   */
  private CodeCraftAiChatService generateAiService(long memoryId) {
    return AiServices.builder(CodeCraftAiChatService.class)
        .chatModel(openAiChatModel)
        .streamingChatModel(openAiStreamingChatModel)
        .chatMemory(generateChatHistory(memoryId))
        .build();
  }

  /**
   * 预生成AI服务构建器 创建AI服务构建器并配置基础模型
   *
   * @param chatModel 聊天模型
   * @param streamingChatModel 流式聊天模型
   * @return AI服务构建器
   */
  private AiServices<CodeCraftAiChatService> preGenerateAiService(
      ChatModel chatModel, StreamingChatModel streamingChatModel) {
    return AiServices.builder(CodeCraftAiChatService.class)
        .chatModel(chatModel)
        .streamingChatModel(streamingChatModel);
  }

  /**
   * 生成聊天历史记录 从数据库加载聊天历史记录并创建消息窗口聊天内存
   *
   * @param memoryId 内存ID，用于加载特定的聊天历史记录
   * @return 消息窗口聊天内存实例
   */
  private MessageWindowChatMemory generateChatHistory(long memoryId) {
    MessageWindowChatMemory chatMemory =
        MessageWindowChatMemory.builder()
            .chatMemoryStore(redisChatMemoryStore)
            .id(memoryId)
            .maxMessages(MAX_MESSAGES)
            .build();
    List<SysChatHistory> messages =
        chatHistoryService.loadChatMemoryMessages(memoryId, MAX_MESSAGES);
    log.debug("从数据库加载 {} 条消息到缓存", messages.size());
    saveToMemory(chatMemory, messages);
    return chatMemory;
  }

  /**
   * 保存消息到内存 将数据库加载的聊天历史记录保存到聊天内存中
   *
   * @param chatMemory 聊天内存实例
   * @param messages 聊天历史记录列表
   */
  private void saveToMemory(ChatMemory chatMemory, List<SysChatHistory> messages) {
    chatMemory.clear();
    messages.forEach(
        message -> {
          switch (MessageTypeEnum.getByValue(message.getMessageType())) {
            case MessageTypeEnum.AI -> chatMemory.add(AiMessage.from(message.getMessage()));
            case MessageTypeEnum.USER -> chatMemory.add(UserMessage.from(message.getMessage()));
            case null -> log.error("未知消息类型: {}", message.getMessageType());
          }
        });
  }
}
