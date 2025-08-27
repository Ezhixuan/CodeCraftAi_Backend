package com.ezhixuan.codeCraftAi_backend.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Description("AI 聊天基础响应")
@Data
public class AiChatBaseResDto {

  @Description("AI 思考结果或对于代码的描述")
  private String result;
}
