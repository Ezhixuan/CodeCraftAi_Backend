package com.ezhixuan.codeCraftAi_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ezhixuan.codeCraftAi_backend.utils.PathUtil;

/**
 * 静态资源配置
 * 配置静态资源访问路径映射
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问路径
        // URL路径: /static/**
        // 实际路径: resources/static/temp/deploy目录下的文件
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/temp/deploy/");
    }
}