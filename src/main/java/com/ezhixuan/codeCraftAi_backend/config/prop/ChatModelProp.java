package com.ezhixuan.codeCraftAi_backend.config.prop;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("langchain4j.open-ai.chat-model")
@Data
public class ChatModelProp {

  @Schema(description = "api key")
  private String apiKey;

  @Schema(description = "baseUrl")
  private String baseUrl;

  @Schema(description = "最大输出 token")
  private int maxTokens = 8192;
}
