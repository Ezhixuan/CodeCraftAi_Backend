package com.ezhixuan.codeCraftAi_backend.domain.dto.chat;

import com.ezhixuan.codeCraftAi_backend.domain.constant.PromptConstant;
import lombok.Getter;

import java.util.Arrays;

import static org.springframework.util.StringUtils.hasText;

/**
 * 对前端发送过来的消息进行预处理后的结果
 *
 * @author Ezhixuan
 * @version 0.0.3beta
 */
@Getter
public class ChatPreProcessedDto {

  private static final String PROMPT_TASK =
"""
<task>标签中是本次你需要解决的核心目标
""";
  private static final String PROMPT_SELECTED =
"""
<selected>标签中是本次你需要处理的代码片段,路由文件信息在<fileContent>标签中
""";

  private static final String PROMPT_EXTRA_TASK =
"""
<extra>标签中是本次你需要处理的额外信息
""";

  private static final String PROMPT_PATH =
"""
<path>标签中是该应用中的所有文件相对路径
""";

  private final String originalContent;

  private final String selectedContent;

  private final String userContent;

  private String fileContent;

  private String extraContent;

  private String pathContent;

  /**
   * 构造方法，从内容中提取任务和选中内容
   *
   * @param content 原始内容
   */
  public ChatPreProcessedDto(String content) {
    this.originalContent = content;

    this.userContent = extractContent(content, "<task>", "</task>");

    this.selectedContent = extractContent(content, "<selected>", "</selected>");
    if (hasText(selectedContent)) {
      setExtraContent(PromptConstant.SELECTED_ELEMENT);
    }

    this.fileContent = extractContent(content, "<fileContent>", "</fileContent>");

    this.extraContent = extractContent(content, "<extra>", "</extra>");

    this.pathContent = extractContent(content, "<path>", "</path>");
  }

  /**
   * 从文本中提取指定标签之间的内容
   *
   * @param text 原始文本
   * @param startTag 开始标签
   * @param endTag 结束标签
   * @return 提取的内容，如果未找到则返回空字符串
   */
  private String extractContent(String text, String startTag, String endTag) {
    int startIndex = text.indexOf(startTag);
    if (startIndex == -1) {
      return "";
    }

    int endIndex = text.indexOf(endTag, startIndex + startTag.length());
    if (endIndex == -1) {
      return "";
    }

    return text.substring(startIndex + startTag.length(), endIndex);
  }

  public void setFileContent(String... fileContents) {
    Arrays.stream(fileContents)
        .forEach(
            fileContent -> {
              if (!this.fileContent.contains(fileContent)) {
                this.fileContent += "\n" + fileContent;
              }
            });
  }

  public void setExtraContent(String... extraContents) {
    Arrays.stream(extraContents)
        .forEach(
            extraContent -> {
              if (!this.extraContent.contains(extraContent)) {
                this.extraContent += "\n" + extraContent;
              }
            });
  }

  public void setPathContent(String... pathContents) {
    Arrays.stream(pathContents)
        .forEach(
            pathContent -> {
              if (!this.pathContent.contains(pathContent)) {
                this.pathContent += "\n" + pathContent;
              }
            });
  }

  public String getSendContent() {
    StringBuilder sb = new StringBuilder();
    // 提示词区域
    sb.append(PROMPT_TASK);
    if (hasText(selectedContent)) {
      sb.append(",").append(PROMPT_SELECTED);
    }
    if (hasText(extraContent)) {
      sb.append(",").append(PROMPT_EXTRA_TASK);
    }
    sb.append("\n");

    // 用户任务区域
    sb.append("<task>").append(userContent).append("</task>").append("\n");
    if (hasText(extraContent)) {
      sb.append("<extra>").append(extraContent).append("</extra>").append("\n");
    }

    // 信息提供区域
    if (hasText(selectedContent)) {
      sb.append("<selected>").append(selectedContent).append("</selected>").append("\n");
    }
    if (hasText(fileContent)) {
      sb.append("<fileContent>").append(fileContent).append("</fileContent>").append("\n");
    }
    if (hasText(pathContent)) {
      sb.append("<path>").append(pathContent).append("</path>").append("\n");
    }
    return sb.toString();
  }
}
