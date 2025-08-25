package com.ezhixuan.codeCraftAi_backend.config;

import com.ezhixuan.codeCraftAi_backend.config.prop.ChatModelProp;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatModelConfig {

  public static final String POWERFUL_AI_SERVICE_MODEL = "deepseek-reasoner";
  @Resource private ChatModelProp chatModelProp;

  @Bean
  StreamingChatModel powerfulStreamingChatModel() {
    return OpenAiStreamingChatModel.builder()
        .apiKey(chatModelProp.getApiKey())
        .baseUrl(chatModelProp.getBaseUrl())
        .modelName(POWERFUL_AI_SERVICE_MODEL)
        .maxTokens(chatModelProp.getMaxTokens())
        .logRequests(true)
        .logResponses(true)
        .build();
  }
}
