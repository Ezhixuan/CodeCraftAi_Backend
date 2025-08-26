package com.ezhixuan.codeCraftAi_backend.ai.tools;

import dev.langchain4j.service.tool.ToolExecution;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ToolResHandler {

    public static String handleToolRes(ToolEnum toolEnum, ToolExecution toolExecution) {
        return switch (toolEnum) {
            case WRITE_TOOL -> FileSaveTool.writeToolRes(toolExecution);
            case UNKNOWN_TOOL -> {
                log.error("未知工具");
                yield  String.format("\n\n[%s] %s\n\n", toolEnum.getText(), toolExecution.result());
            }
        };
    }
}
