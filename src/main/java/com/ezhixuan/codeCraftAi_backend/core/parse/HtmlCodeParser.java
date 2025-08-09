package com.ezhixuan.codeCraftAi_backend.core.parse;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class HtmlCodeParser extends CodeParser<AiChatHtmlResDto> {

    // HTML代码块匹配正则表达式
    private static final Pattern HTML_PATTERN = Pattern.compile(
            "```html\\s*\\n([\\s\\S]*?)\\n```",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 消息解析
     *
     * @param content 消息内容
     * @return AiChatHtmlResDto 解析结果
     * @author Ezhixuan
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
     * 从内容中提取指定类型的代码块
     *
     * @param content 待解析内容
     * @return 提取的代码，如果未找到则返回null
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
