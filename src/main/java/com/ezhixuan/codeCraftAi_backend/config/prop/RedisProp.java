package com.ezhixuan.codeCraftAi_backend.config.prop;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("spring.data.redis")
public class RedisProp {

    @Schema(description = "redis host")
    private String host = "localhost";

    @Schema(description = "redis port")
    private int port = 6379;

    @Schema(description = "LangChain 记忆过期时间")
    private long ttl = 3600;
}
