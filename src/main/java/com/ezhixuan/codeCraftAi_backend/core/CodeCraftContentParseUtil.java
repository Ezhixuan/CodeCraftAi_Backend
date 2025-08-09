package com.ezhixuan.codeCraftAi_backend.core;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于将 String 类型的内容中的代码块进行解析，并返回解析后的内容
 */
@UtilityClass
@Slf4j
public class CodeCraftContentParseUtil {

    // HTML代码块匹配正则表达式
    private static final Pattern HTML_PATTERN = Pattern.compile(
        "```html\\s*\\n([\\s\\S]*?)\\n```", 
        Pattern.CASE_INSENSITIVE
    );
    
    // CSS代码块匹配正则表达式
    private static final Pattern CSS_PATTERN = Pattern.compile(
        "```css\\s*\\n([\\s\\S]*?)\\n```", 
        Pattern.CASE_INSENSITIVE
    );
    
    // JavaScript代码块匹配正则表达式
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile(
        "```javascript\\s*\\n([\\s\\S]*?)\\n```", 
        Pattern.CASE_INSENSITIVE
    );
    
    // 备用JavaScript匹配模式（支持js标识）
    private static final Pattern JS_PATTERN = Pattern.compile(
        "```js\\s*\\n([\\s\\S]*?)\\n```", 
        Pattern.CASE_INSENSITIVE
    );

    /**
     * 解析包含HTML代码块的内容
     * @param content 待解析的内容
     * @return AiChatHtmlResDto 解析结果
     */
    public AiChatHtmlResDto parseHtml(String content) {
        AiChatHtmlResDto result = new AiChatHtmlResDto();
        
        if (content == null || content.trim().isEmpty()) {
            log.warn("解析内容为空");
            return result;
        }
        
        try {
            // 提取HTML代码
            String htmlCode = extractCodeBlock(content, HTML_PATTERN);
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
     * 解析包含HTML、CSS、JavaScript代码块的内容
     * @param content 待解析的内容
     * @return AiChatHtmlCssScriptResDto 解析结果
     */
    public AiChatHtmlCssScriptResDto parseHtmlCssScript(String content) {
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
            if (scriptCode == null || scriptCode.trim().isEmpty()) {
                scriptCode = extractCodeBlock(content, JS_PATTERN);
            }
            result.setScriptCode(scriptCode);
            
            // 提取非代码块部分作为描述
            String description = extractDescription(content);
            result.setResult(description);
            
            log.info("HTML+CSS+JS解析完成，HTML: {}, CSS: {}, JS: {}", 
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
     * 从内容中提取指定类型的代码块
     * @param content 待解析内容
     * @param pattern 匹配模式
     * @return 提取的代码，如果未找到则返回null
     */
    private String extractCodeBlock(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String code = matcher.group(1);
            if (code != null) {
                // 清理可能存在的HTML注释（如<!-- index.html -->）
                code = code.replaceAll("^\\s*<!--\\s*[^>]*-->\\s*\\n?", "").trim();
                return code.isEmpty() ? null : code;
            }
        }
        return null;
    }
    
    /**
     * 提取内容中的描述部分（非代码块部分）
     * @param content 原始内容
     * @return 描述内容
     */
    private String extractDescription(String content) {
        if (content == null) {
            return null;
        }
        
        // 移除所有代码块，保留描述文本
        String description = content
            .replaceAll("```html[\\s\\S]*?```", "")
            .replaceAll("```css[\\s\\S]*?```", "")
            .replaceAll("```javascript[\\s\\S]*?```", "")
            .replaceAll("```js[\\s\\S]*?```", "")
            .trim();
            
        return description.isEmpty() ? null : description;
    }

}
