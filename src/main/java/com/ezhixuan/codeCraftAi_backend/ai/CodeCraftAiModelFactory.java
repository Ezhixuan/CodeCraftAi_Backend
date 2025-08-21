package com.ezhixuan.codeCraftAi_backend.ai;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CodeCraftAiModelFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    public CodeCraftAiChatService getAiService(long memoryId) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryStore(redisChatMemoryStore)
                .id(memoryId)
                .maxMessages(10)
                .build();
        return AiServices.builder(CodeCraftAiChatService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }
}
