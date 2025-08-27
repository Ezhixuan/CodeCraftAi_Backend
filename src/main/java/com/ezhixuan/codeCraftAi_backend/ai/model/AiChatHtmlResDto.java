package com.ezhixuan.codeCraftAi_backend.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Description(
    """
        1. 必须将 html 的代码内容存储在 htmlCode 字段中
        2. 对于描述内容存储在result中
        """)
@Data
public class AiChatHtmlResDto {

  @Description("HTML code")
  private String htmlCode;

  @Description("AI 思考结果或对于代码的描述")
  private String result;
}
