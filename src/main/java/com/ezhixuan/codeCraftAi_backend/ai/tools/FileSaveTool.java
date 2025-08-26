package com.ezhixuan.codeCraftAi_backend.ai.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.utils.PathUtil;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class FileSaveTool {

  @Tool("文件写入工具 写入文件到指定路径")
  public String writeTool(
      @P("文件写入的相对路径") String relativeFilePath,
      @P("文件内容") String content,
      @ToolMemoryId long memoryId) {
    try {
      String path = PathUtil.buildPath(PathUtil.TEMP_DIR, CodeGenTypeEnum.VUE_PROJECT, memoryId);
      path = path + File.separator + relativeFilePath;
      FileUtil.writeUtf8String(content, path);
      return String.format("文件%s写入成功", relativeFilePath);
    } catch (Exception e) {
      String errorMessage = String.format("文件%s写入失败%s", relativeFilePath, e.getMessage());
      log.error(errorMessage);
      return errorMessage;
    }
  }

  public static String writeToolRes(ToolExecution toolExecution) {
    JSONObject jsonObject = JSONUtil.parseObj(toolExecution.request().arguments());
    String relativeFilePath = jsonObject.getStr("relativeFilePath");
    String content = jsonObject.getStr("content");
    String suffix = FileUtil.getSuffix(relativeFilePath);
    String res = toolExecution.result();
    ToolEnum writeTool = ToolEnum.WRITE_TOOL;
    String result =
        String.format(
            """
            [%s] %s
            ```%s
            %s
            ```
            """,
            writeTool.getText(), res, suffix, content);
    return String.format("\n\n%s\n\n", result);
  }
}
