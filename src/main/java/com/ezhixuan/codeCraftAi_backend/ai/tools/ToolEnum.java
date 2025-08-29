package com.ezhixuan.codeCraftAi_backend.ai.tools;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 工具枚举类 定义系统中可用的工具类型及其相关信息
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Getter
public enum ToolEnum {
  /** 文件写入工具 */
  WRITE_TOOL("文件写入工具", "writeTool"),
  /** 文件读取工具 */
  READ_TOOL("文件读取工具", "readTool"),
  /** 应用名生成工具 */
  APP_NAME_TOOL("应用名生成工具", "generateAppNameTool"),
  /** 未知工具 */
  UNKNOWN_TOOL("未知工具", "unknownTool");

  @Schema(description = "工具描述")
  private final String text;

  @Schema(description = "工具名称")
  private final String value;

  ToolEnum(String text, String value) {
    this.value = value;
    this.text = text;
  }

  /**
   * 根据工具名称获取工具枚举 遍历所有枚举值，找到与给定工具名称匹配的枚举项，如果未找到则返回UNKNOWN_TOOL
   *
   * @param toolName 工具名称
   * @return 匹配的工具枚举，如果未找到则返回UNKNOWN_TOOL
   */
  public static ToolEnum getByToolName(String toolName) {
    for (ToolEnum value : values()) {
      if (value.getValue().equals(toolName)) {
        return value;
      }
    }
    return UNKNOWN_TOOL;
  }
}
