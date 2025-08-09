package com.ezhixuan.codeCraftAi_backend.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Description("""
        1. 必须将 html 的代码内容存储在 htmlCode 字段中
        2. 必须将 CSS 的代码内容存储在 cssCode 字段中
        3. 必须将 JavaScript 的代码内容存储在 scriptCode 字段中
        4. 对于描述内容存储在result中
        """)
@Data
public class AiChatHtmlCssScriptResDto{

    @Description("HTML code")
    private String htmlCode;

    @Description("CSS code")
    private String cssCode;

    @Description("JavaScript code")
    private String scriptCode;

    @Description("AI 思考结果或对于代码的描述")
    private String result;
}
