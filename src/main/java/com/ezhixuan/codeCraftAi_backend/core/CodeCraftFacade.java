package com.ezhixuan.codeCraftAi_backend.core;

import java.io.File;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.ezhixuan.codeCraftAi_backend.ai.CodeCraftAiChatService;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.core.parse.CodeParserExecutor;
import com.ezhixuan.codeCraftAi_backend.core.saver.FileSaverExecutor;
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
            case HTML -> {
                AiChatHtmlResDto aiChatHtmlResDto = chatService.chatHtml(userMessage);
                yield FileSaverExecutor.executeSave(aiChatHtmlResDto, codeGenType);
            }
            case HTML_MULTI_FILE -> {
                AiChatHtmlCssScriptResDto aiChatHtmlCssScriptResDto = chatService.chatHtmlCssScript(userMessage);
                yield FileSaverExecutor.executeSave(aiChatHtmlCssScriptResDto, codeGenType);
            }
            default -> {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择正确的代码生成模式");
            }
        };
    }

    /**
     * 聊天并保存 (流式)
     *
     * @param userMessage 用户消息
     * @param codeGenType 代码生成类型
     * @return Flux 流
     */
    public Flux<String> chatAndSaveStream(String userMessage, CodeGenTypeEnum codeGenType) {
        if (Objects.isNull(codeGenType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择代码生成模式");
        }
        return switch (codeGenType) {
            case HTML -> {
                Flux<String> stringFlux = chatService.chatHtmlStream(userMessage);
                yield chatAndSaveStream(stringFlux, codeGenType);
            }
            case HTML_MULTI_FILE -> {
                Flux<String> stringFlux = chatService.chatHtmlCssScriptStream(userMessage);
                yield chatAndSaveStream(stringFlux, codeGenType);
            }
            default -> {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择正确的代码生成模式");
            }
        };
    }

    private Flux<String> chatAndSaveStream(Flux<String> chatStream, CodeGenTypeEnum codeGenTypeEnum) {
        StringBuilder content = new StringBuilder();
        return chatStream.doOnNext(content::append).doOnComplete(() -> {
            try {
                FileSaverExecutor.executeSave(CodeParserExecutor.executeParse(content.toString(), codeGenTypeEnum),
                    codeGenTypeEnum);
            } catch (Exception e) {
                log.error("解析 html 失败", e);
            }
        });
    }
}
