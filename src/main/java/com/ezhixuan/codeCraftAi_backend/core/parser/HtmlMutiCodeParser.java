package com.ezhixuan.codeCraftAi_backend.core.parser;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML多文件代码解析器 专门用于解析包含HTML、CSS和JavaScript代码块的内容，提取各类代码和描述文本
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Slf4j
public class HtmlMutiCodeParser extends CodeParser<AiChatHtmlCssScriptResDto> {

  /** HTML代码块匹配正则表达式 用于匹配和提取HTML代码块中的内容 */
  private static final Pattern HTML_PATTERN =
      Pattern.compile("```html\\s*\\n([\\s\\S]*?)\\n```", Pattern.CASE_INSENSITIVE);

  /** CSS代码块匹配正则表达式 用于匹配和提取CSS代码块中的内容 */
  private static final Pattern CSS_PATTERN =
      Pattern.compile("```css\\s*\\n([\\s\\S]*?)\\n```", Pattern.CASE_INSENSITIVE);

  /** JavaScript代码块匹配正则表达式 用于匹配和提取JavaScript代码块中的内容，支持javascript和js两种标记 */
  private static final Pattern JAVASCRIPT_PATTERN =
      Pattern.compile("```(?:javascript|js)\\s*\\n([\\s\\S]*?)\\n```", Pattern.CASE_INSENSITIVE);

  /**
   * 解析HTML+CSS+JavaScript内容 从给定内容中提取HTML、CSS、JavaScript代码块和描述文本，并封装到AiChatHtmlCssScriptResDto对象中
   *
   * @param content 待解析的内容，可能包含HTML、CSS、JavaScript代码块和描述文本
   * @return 解析结果，包含各类代码和描述文本的AiChatHtmlCssScriptResDto对象
   */
  @Override
  public AiChatHtmlCssScriptResDto parse(String content) {
    AiChatHtmlCssScriptResDto result = new AiChatHtmlCssScriptResDto();

    if (content == null || content.trim().isEmpty()) {
      log.warn("解析内容为空");
      return result;
    }

    try {
      // 提取HTML代码
      String htmlCode = extractCodeBlock(content, HTML_PATTERN);
      result.setHtmlCode(htmlCode);

      // 提取CSS代码
      String cssCode = extractCodeBlock(content, CSS_PATTERN);
      result.setCssCode(cssCode);

      // 提取JavaScript代码（优先匹配javascript，其次匹配js）
      String scriptCode = extractCodeBlock(content, JAVASCRIPT_PATTERN);
      result.setScriptCode(scriptCode);

      // 提取非代码块部分作为描述
      String description = extractDescription(content);
      result.setResult(description);

      log.info(
          "HTML+CSS+JS解析完成，HTML: {}, CSS: {}, JS: {}",
          htmlCode != null ? htmlCode.length() : 0,
          cssCode != null ? cssCode.length() : 0,
          scriptCode != null ? scriptCode.length() : 0);

    } catch (Exception e) {
      log.error("解析HTML+CSS+JS内容时发生错误", e);
      result.setResult("解析过程中发生错误: " + e.getMessage());
    }

    return result;
  }

  /**
   * 提取代码块内容 使用指定的正则表达式模式从内容中提取代码块
   *
   * @param content 包含代码块的原始内容
   * @param pattern 用于匹配代码块的正则表达式模式
   * @return 提取的代码，如果未找到则返回null
   */
  protected String extractCodeBlock(String content, Pattern pattern) {
    Matcher matcher = pattern.matcher(content);
    if (matcher.find()) {
      String code = matcher.group(1);
      if (code != null) {
        return code.isEmpty() ? null : code;
      }
    }
    return null;
  }
}
