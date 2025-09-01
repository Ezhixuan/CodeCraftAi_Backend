package com.ezhixuan.codeCraftAi_backend.core;

import com.ezhixuan.codeCraftAi_backend.ai.CodeCraftAiChatService;
import com.ezhixuan.codeCraftAi_backend.ai.CodeCraftAiModelFactory;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.ai.tools.ToolEnum;
import com.ezhixuan.codeCraftAi_backend.ai.tools.ToolResHandler;
import com.ezhixuan.codeCraftAi_backend.core.parser.CodeParserExecutor;
import com.ezhixuan.codeCraftAi_backend.core.saver.FileSaverExecutor;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import dev.langchain4j.service.TokenStream;
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

/**
 * 代码工匠门面类 提供AI代码生成和保存的核心业务功能，封装了聊天、解析和保存的完整流程 支持同步和异步流式处理两种模式
 *
 * @author ezhixuan
 * @version 0.0.3beta
 * @since 0.0.1beta
 */
@Service
@Slf4j
@Validated
public class CodeCraftFacade {

  @Resource private CodeCraftAiModelFactory aiModelFactory;

  /**
   * 聊天并保存生成的代码
   * 根据代码生成类型调用相应的AI服务生成代码，并保存到文件系统
   *
   * @param userMessage 用户输入的消息
   * @param codeGenType 代码生成类型枚举
   * @param appId 应用ID
   * @return 保存的文件对象
   * @throws BusinessException 当参数错误或生成模式不支持时抛出业务异常
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
      default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择正确的代码生成模式");
    };
  }

  /**
   * 流式聊天并保存生成的代码
   * 根据代码生成类型调用相应的AI服务生成代码流，并在完成后保存到文件系统
   *
   * @param userMessage 用户输入的消息
   * @param codeGenType 代码生成类型枚举
   * @param appId 应用ID
   * @return 包含生成内容的Flux流
   * @throws BusinessException 当参数错误或生成模式不支持时抛出业务异常
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
        TokenStream stringFlux = chatService.chatVueProjectStream(appId, userMessage);
        yield convertToFlux(stringFlux);
      }
    };
  }

  /**
   * 路由聊天服务，根据用户消息判断代码生成类型<br>
   * 通过AI模型分析用户需求，自动识别应该使用的代码生成模式<br>
   * 当AI模型分析失败时，使用指定的默认代码生成类型
   *
   * @since 0.0.3beta
   * @param userMessage 用户输入的消息内容，不能为空
   * @param defaultType 当AI模型分析失败时使用的默认代码生成类型
   * @return CodeGenTypeEnum 代码生成类型枚举，用于确定后续处理流程
   */
  public CodeGenTypeEnum chatRouter(@NotBlank String userMessage, CodeGenTypeEnum defaultType) {
    try {
      CodeCraftAiChatService chatService = aiModelFactory.getRouterAiService();
      return chatService.chatRouter(userMessage);
    } catch (Exception exception) {
      return defaultType;
    }
  }

  /**
   * 流式聊天并保存的处理方法
   * 收集流中的内容，在流完成时进行解析和保存操作
   *
   * @param chatStream 聊天内容流
   * @param codeGenTypeEnum 代码生成类型枚举
   * @param appId 应用ID
   * @return 包含生成内容的Flux流
   */
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

  /**
   * 将TokenStream转换为Flux流
   * 处理AI服务返回的TokenStream，转换为Flux流并处理工具执行相关事件
   *
   * @param tokenStream AI服务返回的TokenStream
   * @return 包含生成内容的Flux流
   */
  private Flux<String> convertToFlux(TokenStream tokenStream) {
    return Flux.create(
        sink -> tokenStream
            .onPartialResponse(sink::next)
            .beforeToolExecution(
                (beforeToolExecution) -> {
                  String toolName = beforeToolExecution.request().name();
                  ToolEnum tool = ToolEnum.getByToolName(toolName);
                  String result = String.format("\n\n调用[%s]进行操作\n\n", tool.getText());
                  sink.next(result);
                })
            .onToolExecuted(
                (toolExecution) -> {
                  String toolName = toolExecution.request().name();
                  ToolEnum tool = ToolEnum.getByToolName(toolName);
                  sink.next(ToolResHandler.handleToolRes(tool, toolExecution));
                })
            .onCompleteResponse((completeResponse) -> sink.complete())
            .onError(sink::error)
            .start());
  }
}
