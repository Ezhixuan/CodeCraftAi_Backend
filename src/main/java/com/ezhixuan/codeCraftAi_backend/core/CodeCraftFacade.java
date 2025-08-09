package com.ezhixuan.codeCraftAi_backend.core;

import java.io.File;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.ezhixuan.codeCraftAi_backend.ai.CodeCraftAiChatService;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class CodeCraftFacade {

    @Resource
    private CodeCraftAiChatService chatService;

    /**
     * 聊天并保存
     *
     * @author Ezhixuan
     * @param userMessage 用户消息
     * @param codeGenType 代码生成模式
     * @return 文件
     */
    public File chatAndSave(String userMessage, CodeGenTypeEnum codeGenType) {
        if (Objects.isNull(codeGenType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择代码生成模式");
        }
        return switch (codeGenType) {
            case HTML -> chatAndSaveHtml(userMessage);
            case HTML_MULTI_FILE -> chatAndSaveHtmlMultiFile(userMessage);
            default -> {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择正确的代码生成模式");
            }
        };
    }

    public Flux<String> chatAndSaveStream(String userMessage, CodeGenTypeEnum codeGenType) {
        if (Objects.isNull(codeGenType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择代码生成模式");
        }
        return switch (codeGenType) {
            case HTML -> chatAndSaveHtmlStream(userMessage);
            case HTML_MULTI_FILE -> chatAndSaveHtmlMultiFileStream(userMessage);
            default -> {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择正确的代码生成模式");
            }
        };
    }

    /**
     * 聊天并保存 单个 html 文件
     *
     * @author Ezhixuan
     * @param userMessage 用户消息
     * @return 文件
     */
    private File chatAndSaveHtml(String userMessage) {
        AiChatHtmlResDto aiChatHtmlResDto = chatService.chatHtml(userMessage);
        return CodeFileProducer.produce(aiChatHtmlResDto);
    }

    /**
     * 聊天并保存 以 html, css, script 三个文件
     *
     * @author Ezhixuan
     * @param userMessage 用户消息
     * @return 文件
     */
    private File chatAndSaveHtmlMultiFile(String userMessage) {
        AiChatHtmlCssScriptResDto aiChatHtmlCssScriptResDto = chatService.chatHtmlCssScript(userMessage);
        return CodeFileProducer.produce(aiChatHtmlCssScriptResDto);
    }

    /**
     * 聊天并保存 单个 html 文件 (流式)
     *
     * @author Ezhixuan
     * @param userMessage 用户消息
     * @return 文件
     */
    private Flux<String> chatAndSaveHtmlStream(String userMessage) {
        Flux<String> chatHtmlStream = chatService.chatHtmlStream(userMessage);
        StringBuilder content = new StringBuilder();
        return chatHtmlStream.doOnNext(content::append).doOnComplete(() -> {
            try {
                File file = CodeFileProducer.produce(CodeCraftContentParseUtil.parseHtml(content.toString()));
                log.info("保存文件成功：{}", file.getAbsolutePath());
            } catch (Exception e) {
                log.error("解析 html 失败", e);
            }
        });
    }

    /**
     * 聊天并保存 以 html, css, script 三个文件 (流式)
     *
     * @author Ezhixuan
     * @param userMessage 用户消息
     * @return 文件
     */
    private Flux<String> chatAndSaveHtmlMultiFileStream(String userMessage) {
        Flux<String> chatHtmlCssScriptStream = chatService.chatHtmlCssScriptStream(userMessage);
        StringBuilder content = new StringBuilder();
        return chatHtmlCssScriptStream.doOnNext(content::append).doOnComplete(() -> {
            try {
                File file = CodeFileProducer.produce(CodeCraftContentParseUtil.parseHtmlCssScript(content.toString()));
                log.info("保存文件成功：{}", file.getAbsolutePath());
            } catch (Exception e) {
                log.error("解析 html 失败", e);
            }
        });
    }
}
