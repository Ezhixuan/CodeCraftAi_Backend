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

@Service
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
}
