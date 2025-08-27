package com.ezhixuan.codeCraftAi_backend.core.parser;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML代码解析器 专门用于解析包含HTML代码块的内容，提取HTML代码和描述文本
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Slf4j
public class HtmlCodeParser extends CodeParser<AiChatHtmlResDto> {

  /** HTML代码块匹配正则表达式 用于匹配和提取HTML代码块中的内容 */
  private static final Pattern HTML_PATTERN =
      Pattern.compile("```html\\s*\\n([\\s\\S]*?)\\n```", Pattern.CASE_INSENSITIVE);

  /**
   * 解析HTML内容 从给定内容中提取HTML代码块和描述文本，并封装到AiChatHtmlResDto对象中
   *
   * @param content 待解析的内容，可能包含HTML代码块和描述文本
   * @return 解析结果，包含HTML代码和描述文本的AiChatHtmlResDto对象
   */
  @Override
  public AiChatHtmlResDto parse(String content) {
    AiChatHtmlResDto result = new AiChatHtmlResDto();

    if (content == null || content.trim().isEmpty()) {
      log.warn("解析内容为空");
      return result;
    }

    try {
      // 提取HTML代码
      String htmlCode = extractCodeBlock(content);
      result.setHtmlCode(htmlCode);

      // 提取非代码块部分作为描述
      String description = extractDescription(content);
      result.setResult(description);

      log.info("HTML解析完成，HTML代码长度: {}", htmlCode != null ? htmlCode.length() : 0);

    } catch (Exception e) {
      log.error("解析HTML内容时发生错误", e);
      result.setResult("解析过程中发生错误: " + e.getMessage());
    }

    return result;
  }

  /**
   * 提取代码块内容 使用正则表达式从内容中提取HTML代码块
   *
   * @param content 包含代码块的原始内容
   * @return 提取的HTML代码，如果未找到则返回null
   */
  protected String extractCodeBlock(String content) {
    Matcher matcher = HtmlCodeParser.HTML_PATTERN.matcher(content);
    if (matcher.find()) {
      String code = matcher.group(1);
      if (code != null) {
        return code.isEmpty() ? null : code;
      }
    }
    return null;
  }
}
