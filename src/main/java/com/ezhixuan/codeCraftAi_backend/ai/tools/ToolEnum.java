package com.ezhixuan.codeCraftAi_backend.ai.tools;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public enum ToolEnum {
  WRITE_TOOL("文件写入工具", "writeTool"),
  UNKNOWN_TOOL("未知工具", "unknownTool");

  @Schema(description = "工具描述")
  private final String text;

  @Schema(description = "工具名称")
  private final String value;

  ToolEnum(String text, String value) {
    this.value = value;
    this.text = text;
  }

  public static ToolEnum getByToolName(String toolName) {
    for (ToolEnum value : values()) {
      if (value.getValue().equals(toolName)) {
        return value;
      }
    }
    return UNKNOWN_TOOL;
  }
}
