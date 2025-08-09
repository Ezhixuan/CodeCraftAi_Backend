package com.ezhixuan.codeCraftAi_backend.core;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CodeCraftContentParseUtilTest {

    @Test
    void testParseHtml() {
        String content = "这是一个简单的HTML页面示例：\n\n" +
                "```html\n" +
                "<!-- index.html -->\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Hello World</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Hello World</h1>\n" +
                "</body>\n" +
                "</html>\n" +
                "```\n\n" +
                "这是页面的描述信息。";

        AiChatHtmlResDto result = CodeCraftContentParseUtil.parseHtml(content);
        
        assertNotNull(result);
        assertNotNull(result.getHtmlCode());
        assertTrue(result.getHtmlCode().contains("Hello World"));
        assertTrue(result.getHtmlCode().contains("<!DOCTYPE html>"));
        assertNotNull(result.getResult());
        assertTrue(result.getResult().contains("简单的HTML页面示例"));
    }

    @Test
    void testParseHtmlCssScript() {
        String content = "这是一个完整的网页示例，包含HTML、CSS和JavaScript：\n\n" +
                "```html\n" +
                "<!-- index.html -->\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>测试页面</title>\n" +
                "    <link rel=\"stylesheet\" href=\"style.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1 id=\"title\">点击我</h1>\n" +
                "    <script src=\"script.js\"></script>\n" +
                "</body>\n" +
                "</html>\n" +
                "```\n\n" +
                "```css\n" +
                "<!-- style.css -->\n" +
                "body {\n" +
                "    font-family: Arial, sans-serif;\n" +
                "    background-color: #f0f0f0;\n" +
                "}\n" +
                "\n" +
                "#title {\n" +
                "    color: blue;\n" +
                "    cursor: pointer;\n" +
                "}\n" +
                "```\n\n" +
                "```javascript\n" +
                "<!-- script.js -->\n" +
                "document.addEventListener('DOMContentLoaded', function() {\n" +
                "    const title = document.getElementById('title');\n" +
                "    title.addEventListener('click', function() {\n" +
                "        alert('你好世界！');\n" +
                "    });\n" +
                "});\n" +
                "```\n\n" +
                "这个示例展示了如何创建一个交互式网页。";

        AiChatHtmlCssScriptResDto result = CodeCraftContentParseUtil.parseHtmlCssScript(content);
        
        assertNotNull(result);
        
        // 验证HTML代码
        assertNotNull(result.getHtmlCode());
        assertTrue(result.getHtmlCode().contains("<!DOCTYPE html>"));
        assertTrue(result.getHtmlCode().contains("点击我"));
        
        // 验证CSS代码
        assertNotNull(result.getCssCode());
        assertTrue(result.getCssCode().contains("font-family: Arial"));
        assertTrue(result.getCssCode().contains("color: blue"));
        
        // 验证JavaScript代码
        assertNotNull(result.getScriptCode());
        assertTrue(result.getScriptCode().contains("addEventListener"));
        assertTrue(result.getScriptCode().contains("你好世界"));
        
        // 验证描述
        assertNotNull(result.getResult());
        assertTrue(result.getResult().contains("完整的网页示例"));
    }

    @Test
    void testParseWithJsTag() {
        String content = "使用js标签的示例：\n\n" +
                "```html\n" +
                "<html><body><h1>测试</h1></body></html>\n" +
                "```\n\n" +
                "```css\n" +
                "body { margin: 0; }\n" +
                "```\n\n" +
                "```js\n" +
                "console.log('Hello');\n" +
                "```";

        AiChatHtmlCssScriptResDto result = CodeCraftContentParseUtil.parseHtmlCssScript(content);
        
        assertNotNull(result);
        assertNotNull(result.getHtmlCode());
        assertNotNull(result.getCssCode());
        assertNotNull(result.getScriptCode());
        assertTrue(result.getScriptCode().contains("console.log"));
    }

    @Test
    void testParseEmptyContent() {
        AiChatHtmlResDto htmlResult = CodeCraftContentParseUtil.parseHtml("");
        assertNotNull(htmlResult);
        
        AiChatHtmlCssScriptResDto fullResult = CodeCraftContentParseUtil.parseHtmlCssScript(null);
        assertNotNull(fullResult);
    }

    @Test
    void testParseWithoutCodeBlocks() {
        String content = "这只是一段普通的描述文本，没有任何代码块。";
        
        AiChatHtmlResDto result = CodeCraftContentParseUtil.parseHtml(content);
        
        assertNotNull(result);
        assertNull(result.getHtmlCode());
        assertEquals(content, result.getResult());
    }

    @Test
    void testParseWithoutComments() {
        String content = "没有注释的代码块示例：\n\n" +
                "```html\n" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head><title>无注释测试</title></head>\n" +
                "<body><h1>测试页面</h1></body>\n" +
                "</html>\n" +
                "```\n\n" +
                "```css\n" +
                "body { margin: 0; padding: 20px; }\n" +
                "h1 { color: red; }\n" +
                "```\n\n" +
                "```javascript\n" +
                "console.log('没有注释的JS代码');\n" +
                "document.title = '动态标题';\n" +
                "```";

        AiChatHtmlCssScriptResDto result = CodeCraftContentParseUtil.parseHtmlCssScript(content);
        
        assertNotNull(result);
        assertNotNull(result.getHtmlCode());
        assertNotNull(result.getCssCode());
        assertNotNull(result.getScriptCode());
        
        assertTrue(result.getHtmlCode().contains("无注释测试"));
        assertTrue(result.getCssCode().contains("margin: 0"));
        assertTrue(result.getScriptCode().contains("没有注释的JS代码"));
        
        assertNotNull(result.getResult());
        assertTrue(result.getResult().contains("没有注释的代码块示例"));
    }
}