package com.ezhixuan.codeCraftAi_backend.ai;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.ai.tools.FileSaveTool;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CodeCraftAiModelFactory {

    private static final int MAX_MESSAGES = 25;
    private final ChatModel chatModel;
    private final StreamingChatModel openAiStreamingChatModel;
    private final StreamingChatModel powerfulStreamingChatModel;
    private final RedisChatMemoryStore redisChatMemoryStore;
    private final SysChatHistoryService chatHistoryService;
    /**
     * 缓存 AI 服务
     */
    private final LoadingCache<Long, CodeCraftAiChatService> AI_SERVICE_CACHE
            = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(Duration.ofMinutes(10))
            .expireAfterWrite(Duration.ofMinutes(30))
            .removalListener((key, value, cause) -> {
                log.debug("缓存 AI 服务被移除，key: {}, value: {}, cause: {}", key, value, cause);
            })
            .build(this::generateAiService);

    /**
     * 获取 AI 服务
     * @author Ezhixuan
     * @param memoryId 缓存 id
     * @return CodeCraftAiChatService AiService
     */
    public CodeCraftAiChatService getAiService(long memoryId) {
        return getAiService(memoryId, CodeGenTypeEnum.HTML_MULTI_FILE);
    }

    /**
     * 通过 memoryId 与 CodeGenType 获取 AiService
     * @author Ezhixuan
     * @param memoryId 缓存 id
     * @param codeGenTypeEnum 代码生成类型
     * @return CodeCraftAiChatService
     */
    public CodeCraftAiChatService getAiService(long memoryId, CodeGenTypeEnum codeGenTypeEnum) {
        return switch (codeGenTypeEnum) {
            case HTML, HTML_MULTI_FILE -> AI_SERVICE_CACHE.get(memoryId);
            case VUE_PROJECT ->
                    AI_SERVICE_CACHE.get(memoryId, key ->
                            preGenerateAiService(chatModel, powerfulStreamingChatModel)
                                    .chatMemoryProvider(chatMemoryId -> generateChatHistory(memoryId))
                                    .tools(new FileSaveTool())
                                    .hallucinatedToolNameStrategy(toolExecutionRequest ->
                                            ToolExecutionResultMessage.from(
                                                    toolExecutionRequest,
                                                    "错误：没有名为 " + toolExecutionRequest.name() + " 的工具"))
                                    .build());
        };
    }

    /**
     * 创建 Ai 服务
     * @author Ezhixuan
     * @param memoryId 缓存 id
     * @return CodeCraftAiChatService AiService
     */
    private CodeCraftAiChatService generateAiService(long memoryId) {
        return AiServices.builder(CodeCraftAiChatService.class)
                .chatModel(chatModel)
                .streamingChatModel(openAiStreamingChatModel)
                .chatMemory(generateChatHistory(memoryId))
                .build();
    }

    /**
     * 预创建 codeCraftAiChatService 的模型,后续配置由调用者自行调整
     * @author Ezhixuan
     * @param chatModel 对话模型
     * @param streamingChatModel 流式对话模型
     * @return AiServices<CodeCraftAiChatService>
     */
    private AiServices<CodeCraftAiChatService> preGenerateAiService(ChatModel chatModel, StreamingChatModel streamingChatModel) {
        return AiServices.builder(CodeCraftAiChatService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel);
    }

    /**
     * 创建缓存
     * @author Ezhixuan
     * @param memoryId 缓存 id
     * @return MessageWindowChatMemory 缓存
     */
    private MessageWindowChatMemory generateChatHistory(long memoryId) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryStore(redisChatMemoryStore)
                .id(memoryId)
                .maxMessages(MAX_MESSAGES)
                .build();
        List<SysChatHistory> messages = chatHistoryService.loadChatMemoryMessages(memoryId, MAX_MESSAGES);
        log.debug("从数据库加载 {} 条消息到缓存", messages.size());
        saveToMemory(chatMemory, messages);
        return chatMemory;
    }

    /**
     * 保存消息到内存
     * @author Ezhixuan
     * @param chatMemory 缓存
     * @param messages 消息
     */
    private void saveToMemory(ChatMemory chatMemory, List<SysChatHistory> messages) {
        chatMemory.clear();
        messages.forEach(message -> {
            switch (MessageTypeEnum.getByType(message.getMessageType())) {
                case MessageTypeEnum.AI -> {
                    chatMemory.add(AiMessage.from(message.getMessage()));
                }
                case MessageTypeEnum.USER -> {
                    chatMemory.add(UserMessage.from(message.getMessage()));
                }
                case null -> {
                    log.error("未知消息类型: {}", message.getMessageType());
                }
            }
        });
    }
}
