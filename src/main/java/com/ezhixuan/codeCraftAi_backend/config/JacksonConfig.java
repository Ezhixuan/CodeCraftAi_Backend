package com.ezhixuan.codeCraftAi_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    // 配置时间格式
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    JavaTimeModule timeModule = new JavaTimeModule();
    timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
    timeModule.addDeserializer(
        LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
    // 配置Long类型序列化为String，避免前端精度丢失
    SimpleModule longModule = new SimpleModule();
    longModule.addSerializer(Long.class, ToStringSerializer.instance);
    longModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

    return Jackson2ObjectMapperBuilder.json().modules(timeModule, longModule).build();
  }
}
