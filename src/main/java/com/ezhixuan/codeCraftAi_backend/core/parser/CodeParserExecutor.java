package com.ezhixuan.codeCraftAi_backend.core.parser;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import lombok.experimental.UtilityClass;

/**
 * 代码解析执行器 负责根据代码生成类型选择相应的代码解析器并执行解析操作 这是一个工具类，提供静态方法供外部调用
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@UtilityClass
public class CodeParserExecutor {

  /** HTML代码解析器实例 */
  private final CodeParser<AiChatHtmlResDto> HTML_CODE_PARSER = new HtmlCodeParser();

  /** HTML多文件代码解析器实例 */
  private final CodeParser<AiChatHtmlCssScriptResDto> HTML_CSS_SCRIPT_PARSER =
      new HtmlMutiCodeParser();

  /**
   * 执行代码解析 根据代码生成类型选择相应的解析器并执行解析操作
   *
   * @param content 待解析的内容
   * @param codeGenTypeEnum 代码生成类型枚举
   * @return 解析结果对象
   * @throws IllegalArgumentException 当不支持的代码生成类型时抛出异常
   */
  public Object executeParse(String content, CodeGenTypeEnum codeGenTypeEnum) {
    return switch (codeGenTypeEnum) {
      case HTML -> HTML_CODE_PARSER.parse(content);
      case HTML_MULTI_FILE -> HTML_CSS_SCRIPT_PARSER.parse(content);
      default -> throw new IllegalArgumentException("不支持的代码生成类型: " + codeGenTypeEnum);
    };
  }
}
