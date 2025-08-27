package com.ezhixuan.codeCraftAi_backend.ai.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.ezhixuan.codeCraftAi_backend.ai.CodeCraftAiChatService;

import jakarta.annotation.Resource;

@SpringBootTest
class CodeCraftAiModelFactoryTest {

    @Resource
    private CodeCraftAiChatService chatService;

    @Test
    void codeCraftAiChatService() {
        String userMessage = "请生成一个简单的HTML页面，内容是“Hello World”";
        AiChatHtmlResDto response = chatService.chatHtml(userMessage);
        System.out.println(response);
    }

    @Test
    void codeCraftAiChatServiceHtmlCssScript() {
        String userMessage = "请生成一个简单的HTML页面，内容是“Hello World”，并生成对应的CSS和JavaScript代码";
        AiChatHtmlCssScriptResDto response = chatService.chatHtmlCssScript(userMessage);
        System.out.println(response);
    }

}
