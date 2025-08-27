package com.ezhixuan.codeCraftAi_backend.config;

import com.ezhixuan.codeCraftAi_backend.config.prop.RedisProp;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

  @Resource private RedisProp redisProp;

  @Bean
  public RedisChatMemoryStore redisChatMemoryStore() {
    return RedisChatMemoryStore.builder()
        .host(redisProp.getHost())
        .port(redisProp.getPort())
        .ttl(redisProp.getTtl())
        .build();
  }
}
