package com.ezhixuan.ai.codeCraftAi_backend.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("system")
@Data
@Configuration
public class SystemProp {

    private String salt;
}
