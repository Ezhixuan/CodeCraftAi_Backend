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

/**
 * 文件保存工具类 提供将AI生成的代码内容写入文件的功能，支持按相对路径保存到指定目录 主要用于Vue项目的代码生成和保存
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Slf4j
public class FileSaveTool {

  /**
   * 工具执行结果格式化方法 将工具执行的结果格式化为可读的字符串格式，包含文件路径、内容和执行结果
   *
   * @param toolExecution 工具执行对象，包含请求参数和执行结果
   * @return 格式化后的执行结果字符串
   */
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

  /**
   * 文件写入工具方法 将指定内容写入到指定路径的文件中，路径基于项目生成的特定目录
   *
   * @param relativeFilePath 文件写入的相对路径（相对于项目生成目录）
   * @param content 文件内容
   * @param memoryId 工具内存ID，用于构建唯一的项目目录
   * @return 操作结果信息，成功或失败信息
   */
  @Tool("文件写入工具 写入文件到指定路径")
  public String writeTool(
      @P("文件写入的相对路径") String relativeFilePath,
      @P("文件内容") String content,
      @ToolMemoryId long memoryId) {
    try {
      String path =
          PathUtil.buildPath(PathUtil.ORIGINAL_DIR, CodeGenTypeEnum.VUE_PROJECT, memoryId);
      path = path + File.separator + relativeFilePath;
      FileUtil.writeUtf8String(content, path);
      return String.format("文件%s写入成功", relativeFilePath);
    } catch (Exception e) {
      String errorMessage = String.format("文件%s写入失败%s", relativeFilePath, e.getMessage());
      log.error(errorMessage);
      return errorMessage;
    }
  }
}
