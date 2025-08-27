package com.ezhixuan.codeCraftAi_backend.config.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@ConfigurationProperties("system")
@Data
@Configuration
public class SystemProp {

    @Schema(description = "默认密码")
    private String defaultPassword = "123456";
}
