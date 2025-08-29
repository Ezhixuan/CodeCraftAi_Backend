package com.ezhixuan.codeCraftAi_backend.ai.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.ezhixuan.codeCraftAi_backend.service.SysAppService;
import com.ezhixuan.codeCraftAi_backend.utils.PathUtil;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * 文件工具类 提供将AI生成的代码内容写入文件的功能，支持按相对路径保存到指定目录 主要用于Vue项目的代码生成和保存
 *
 * @author ezhixuan
 * @version 0.0.2beta
 * @since 0.0.1beta FileSaveTool
 */
@Slf4j
@Component
public class FileTool {

  @Resource @Lazy private SysAppService appService;

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
   * 文件读取工具执行结果格式化方法 将文件读取工具的执行结果格式化为可读的字符串格式，包含文件路径和读取的行数范围
   *
   * @since 0.0.2beta
   * @param toolExecution 工具执行对象，包含请求参数和执行结果
   * @return 格式化后的执行结果字符串
   */
  public static String readToolRes(ToolExecution toolExecution) {
    JSONObject jsonObject = JSONUtil.parseObj(toolExecution.request().arguments());
    String relativeFilePath = jsonObject.getStr("relativePath");
    Integer startLine = jsonObject.getInt("startLine");
    Integer endLine = jsonObject.getInt("endLine");
    String res = toolExecution.result();
    ToolEnum readTool = ToolEnum.READ_TOOL;
    String result =
        String.format(
            """
            [%s]
            读取文件: %s
            行数范围: %d - %d
            """,
            readTool.getText(), relativeFilePath, startLine, endLine);
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
      SysApp sysApp = appService.getById(memoryId);
      if (Objects.isNull(sysApp)) {
        return "项目不存在,结束对话";
      }
      String path =
          PathUtil.buildPath(
              PathUtil.ORIGINAL_DIR, CodeGenTypeEnum.getByValue(sysApp.getCodeGenType()), memoryId);
      path = path + File.separator + relativeFilePath;
      FileUtil.writeUtf8String(content, path);
      return String.format("文件%s写入成功", relativeFilePath);
    } catch (Exception e) {
      String errorMessage = String.format("文件%s写入失败%s", relativeFilePath, e.getMessage());
      log.error(errorMessage);
      return errorMessage;
    }
  }

  /**
   * 文件查看工具方法 读取指定文件的指定行数范围内的内容
   *
   * @since 0.0.2beta
   * @param relativePath 需要查看的文件相对路径（相对于项目生成目录）
   * @param startLine 起始行数（从0开始）
   * @param endLine 结束行数（包含该行）
   * @param memoryId 工具内存ID，用于构建唯一的项目目录
   * @return 文件指定行数范围内的内容，或错误信息
   */
  @Tool("文件查看工具 查看指定行数内的内容")
  public String readTool(
      @P("需要查看的文件文件相对路径") String relativePath,
      @P("起始行数") int startLine,
      @P("结束行数") int endLine,
      @ToolMemoryId long memoryId) {
    try {
      SysApp sysApp = appService.getById(memoryId);
      if (Objects.isNull(sysApp)) {
        return "项目不存在,结束对话";
      }
      String basePath =
          PathUtil.buildPath(
              PathUtil.ORIGINAL_DIR, CodeGenTypeEnum.getByValue(sysApp.getCodeGenType()), memoryId);
      String fullPath = basePath + File.separator + relativePath;
      if (!FileUtil.exist(fullPath)) {
        return String.format("文件 %s 不存在", relativePath);
      }

      List<String> lines = FileUtil.readLines(fullPath, "UTF-8");

      // 检查行数范围是否有效
      if (startLine < 0 || endLine < 0 || startLine > endLine || startLine >= lines.size()) {
        return String.format("行数范围无效: 起始行 %d, 结束行 %d, 文件总行数 %d", startLine, endLine, lines.size());
      }

      // 确保结束行不超过文件总行数
      int actualEndLine = Math.min(endLine, lines.size() - 1);

      // 提取指定范围的行
      List<String> resultLines = lines.subList(startLine, actualEndLine + 1);
      StringBuilder result = new StringBuilder();
      for (String resultLine : resultLines) {
        result.append(resultLine).append("\n");
      }
      return result.toString();
    } catch (Exception e) {
      String errorMessage = String.format("读取文件 %s 失败: %s", relativePath, e.getMessage());
      log.error(errorMessage, e);
      return errorMessage;
    }
  }
}
