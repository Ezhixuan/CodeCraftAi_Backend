package com.ezhixuan.codeCraftAi_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@MapperScan("com.ezhixuan.codeCraftAi_backend.mapper")
public class CodeCraftAiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeCraftAiBackendApplication.class, args);
    }

}
