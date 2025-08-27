package com.ezhixuan.codeCraftAi_backend.ai.tools;

import dev.langchain4j.service.tool.ToolExecution;
import lombok.extern.slf4j.Slf4j;

/**
 * 工具执行结果处理器 负责处理不同类型工具的执行结果，根据工具类型调用相应的结果处理方法
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Slf4j
public class ToolResHandler {

  /**
   * 处理工具执行结果的方法 根据工具类型调用对应的处理方法，返回格式化后的结果字符串
   *
   * @param toolEnum 工具枚举类型，标识具体的工具类型
   * @param toolExecution 工具执行对象，包含执行参数和结果
   * @return 格式化后的工具执行结果字符串
   */
  public static String handleToolRes(ToolEnum toolEnum, ToolExecution toolExecution) {
    return switch (toolEnum) {
      case WRITE_TOOL -> FileSaveTool.writeToolRes(toolExecution);
      case UNKNOWN_TOOL -> {
        log.error("未知工具");
        yield String.format("\n\n[%s] %s\n\n", toolEnum.getText(), toolExecution.result());
      }
    };
  }
}
