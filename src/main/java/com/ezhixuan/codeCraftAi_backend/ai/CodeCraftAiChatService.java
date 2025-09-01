package com.ezhixuan.codeCraftAi_backend.ai;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatBaseResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * AI创建 service,用于定义域 ai 对话的接口
 *
 * @author ezhixuan
 * @version 0.0.3beta
 * @since 0.0.1beta
 */
public interface CodeCraftAiChatService {

  /**
   * 返回基础对话信息
   *
   * @author Ezhixuan
   * @param userMessage 用户信息
   * @return AiChatBaseResponse 基础对话信息
   */
  @SystemMessage(fromResource = "prompt/codeCraft-common-generate-system-prompt.txt")
  AiChatBaseResDto chat(String userMessage);

  /**
   * 获取html信息
   *
   * @author Ezhixuan
   * @param userMessage 用户信息
   * @return AiChatHtmlResDto html信息
   */
  @SystemMessage(fromResource = "prompt/codeCraft-html-generate-system-prompt.txt")
  AiChatHtmlResDto chatHtml(String userMessage);

  /**
   * 获取html信息
   *
   * @author Ezhixuan
   * @param userMessage 用户信息
   * @return AiChatHtmlCssScriptResDto html信息
   */
  @SystemMessage(fromResource = "prompt/codeCraft-htmlAcssAjs-generate-system-prompt.txt")
  AiChatHtmlCssScriptResDto chatHtmlCssScript(String userMessage);

  /**
   * 根据用户需求智能判断代码生成模式 通过分析用户需求的复杂度和功能要求，自动选择最适合的代码生成类型
   *
   * @author Ezhixuan
   * @param userMessage 用户的需求描述信息
   * @return CodeGenTypeEnum 代码生成模式枚举值，用于确定后续代码生成的具体方式
   * @since 0.0.3beta
   */
  @SystemMessage(fromResource = "prompt/codeCraft-router-system-prompt.txt")
  CodeGenTypeEnum chatRouter(String userMessage);

  /**
   * 获取html信息 (带流式返回)
   *
   * @author Ezhixuan
   * @param userMessage 用户信息
   * @return AiChatHtmlCssScriptResDto html信息
   */
  @SystemMessage(fromResource = "prompt/codeCraft-common-generate-system-prompt.txt")
  Flux<String> chatStream(String userMessage);

  /**
   * 获取html信息 (带流式返回)
   *
   * @author Ezhixuan
   * @param userMessage 用户信息
   * @return AiChatHtmlCssScriptResDto html信息
   */
  @SystemMessage(fromResource = "prompt/codeCraft-html-generate-system-prompt.txt")
  Flux<String> chatHtmlStream(String userMessage);

  /**
   * 获取html信息 (带流式返回)
   *
   * @author Ezhixuan
   * @param userMessage 用户信息
   * @return AiChatHtmlCssScriptResDto html信息
   */
  @SystemMessage(fromResource = "prompt/codeCraft-htmlAcssAjs-generate-system-prompt.txt")
  Flux<String> chatHtmlCssScriptStream(String userMessage);

  /**
   * vue 工程化项目模式对话
   *
   * @author Ezhixuan
   * @param memoryId 记忆id
   * @param userMessage 用户信息
   * @return AiChatHtmlCssScriptResDto html信息
   */
  @SystemMessage(fromResource = "prompt/codeCraft-vueProject-generate-system-prompt.txt")
  TokenStream chatVueProjectStream(@MemoryId long memoryId, @UserMessage String userMessage);
}
