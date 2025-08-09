package com.ezhixuan.codeCraftAi_backend.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;

@Configuration
public class CodeCraftAiModelFactory {

    @Resource
    private ChatModel chatModel;

    @Bean
    public CodeCraftAiChatService codeCraftAiChatService(){
        return AiServices.builder(CodeCraftAiChatService.class)
                .chatModel(chatModel)
                .build();
    }
}
