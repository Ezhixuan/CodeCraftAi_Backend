package com.ezhixuan.codeCraftAi_backend.ai;

import com.ezhixuan.codeCraftAi_backend.domain.entity.SysChatHistory;
import com.ezhixuan.codeCraftAi_backend.domain.enums.MessageTypeEnum;
import com.ezhixuan.codeCraftAi_backend.service.SysChatHistoryService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.AiMessage;
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

    private final ChatModel chatModel;
    private final StreamingChatModel streamingChatModel;
    private final RedisChatMemoryStore redisChatMemoryStore;
    private final SysChatHistoryService chatHistoryService;

    private static final int MAX_MESSAGES = 10;

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
        return AI_SERVICE_CACHE.get(memoryId);
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
                .streamingChatModel(streamingChatModel)
                .chatMemory(generateChatHistory(memoryId))
                .build();
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
