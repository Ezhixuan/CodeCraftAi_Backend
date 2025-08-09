package com.ezhixuan.codeCraftAi_backend.core.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HtmlMutiCodeParser extends CodeParser<AiChatHtmlCssScriptResDto> {

    // HTML代码块匹配正则表达式
    private static final Pattern HTML_PATTERN = Pattern.compile(
            "```html\\s*\\n([\\s\\S]*?)\\n```",
            Pattern.CASE_INSENSITIVE
    );

    // CSS代码块匹配正则表达式
    private static final Pattern CSS_PATTERN =
        Pattern.compile("```css\\s*\\n([\\s\\S]*?)\\n```", Pattern.CASE_INSENSITIVE);

    // JavaScript代码块匹配正则表达式
    private static final Pattern JAVASCRIPT_PATTERN =
        Pattern.compile("```(?:javascript|js)\\s*\\n([\\s\\S]*?)\\n```", Pattern.CASE_INSENSITIVE);

    /**
     * 消息解析
     *
     * @param content 消息内容
     * @return T
     * @author Ezhixuan
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

            log.info("HTML+CSS+JS解析完成，HTML: {}, CSS: {}, JS: {}", htmlCode != null ? htmlCode.length() : 0,
                cssCode != null ? cssCode.length() : 0, scriptCode != null ? scriptCode.length() : 0);

        } catch (Exception e) {
            log.error("解析HTML+CSS+JS内容时发生错误", e);
            result.setResult("解析过程中发生错误: " + e.getMessage());
        }

        return result;
    }

    /**
     * 从内容中提取指定类型的代码块
     * @param content 待解析内容
     * @param pattern 匹配模式
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
