package com.ezhixuan.codeCraftAi_backend.core;

import com.ezhixuan.codeCraftAi_backend.ai.CodeCraftAiChatService;
import com.ezhixuan.codeCraftAi_backend.ai.CodeCraftAiModelFactory;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.core.parser.CodeParserExecutor;
import com.ezhixuan.codeCraftAi_backend.core.saver.FileSaverExecutor;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.Objects;

@Service
@Slf4j
@Validated
public class CodeCraftFacade {

  @Resource private CodeCraftAiModelFactory aiModelFactory;

  /**
   * 聊天并保存
   *
   * @author Ezhixuan
   * @param userMessage 用户消息
   * @param codeGenType 代码生成模式
   * @return 文件
   */
  public File chatAndSave(
      @NotBlank String userMessage,
      @NotNull CodeGenTypeEnum codeGenType,
      @NotNull @Min(1) Long appId) {
    if (Objects.isNull(codeGenType)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择代码生成模式");
    }
    CodeCraftAiChatService chatService = aiModelFactory.getAiService(appId);
    return switch (codeGenType) {
      case HTML -> {
        AiChatHtmlResDto aiChatHtmlResDto = chatService.chatHtml(userMessage);
        yield FileSaverExecutor.executeSave(aiChatHtmlResDto, codeGenType, appId);
      }
      case HTML_MULTI_FILE -> {
        AiChatHtmlCssScriptResDto aiChatHtmlCssScriptResDto =
            chatService.chatHtmlCssScript(userMessage);
        yield FileSaverExecutor.executeSave(aiChatHtmlCssScriptResDto, codeGenType, appId);
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
  public Flux<String> chatAndSaveStream(
      @NotBlank String userMessage,
      @NotNull CodeGenTypeEnum codeGenType,
      @NotNull @Min(1) Long appId) {
    if (Objects.isNull(codeGenType)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择代码生成模式");
    }
    CodeCraftAiChatService chatService = aiModelFactory.getAiService(appId, codeGenType);
    return switch (codeGenType) {
      case HTML -> {
        Flux<String> stringFlux = chatService.chatHtmlStream(userMessage);
        yield chatAndSaveStream(stringFlux, codeGenType, appId);
      }
      case HTML_MULTI_FILE -> {
        Flux<String> stringFlux = chatService.chatHtmlCssScriptStream(userMessage);
        yield chatAndSaveStream(stringFlux, codeGenType, appId);
      }
      case VUE_PROJECT -> {
        Flux<String> stringFlux = chatService.vueProjectStream(appId, userMessage);
        yield chatAndSaveStream(stringFlux, CodeGenTypeEnum.HTML_MULTI_FILE, appId);
      }
      default -> {
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择正确的代码生成模式");
      }
    };
  }

  private Flux<String> chatAndSaveStream(
      Flux<String> chatStream, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
    StringBuilder content = new StringBuilder();
    return chatStream
        .doOnNext(content::append)
        .doOnComplete(
            () -> {
              try {
                FileSaverExecutor.executeSave(
                    CodeParserExecutor.executeParse(content.toString(), codeGenTypeEnum),
                    codeGenTypeEnum,
                    appId);
              } catch (Exception e) {
                log.error("解析 html 失败", e);
              }
            });
  }
}
