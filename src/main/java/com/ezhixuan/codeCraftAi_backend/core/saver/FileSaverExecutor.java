package com.ezhixuan.codeCraftAi_backend.core.saver;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * 文件保存执行器 负责根据代码生成类型选择相应的文件保存器并执行保存操作 这是一个工具类，提供静态方法供外部调用
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@UtilityClass
public class FileSaverExecutor {

  /** HTML文件保存器实例 */
  private final HtmlFileSaver htmlFileSaver = new HtmlFileSaver();

  /** HTML多文件保存器实例 */
  private final HtmlMutiFileSaver htmlMutiFileSaver = new HtmlMutiFileSaver();

  /**
   * 执行文件保存 根据代码生成类型选择相应的文件保存器并执行保存操作
   *
   * @param resDto 解析后的AI聊天结果对象
   * @param codeGenTypeEnum 代码生成类型枚举
   * @param appId 应用ID
   * @return 保存的文件对象
   * @throws IllegalArgumentException 当不支持的代码生成类型时抛出异常
   */
  public File executeSave(Object resDto, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
    FileSaverDto saverDto =
        FileSaverDto.builder()
            .aiChatDto(resDto)
            .codeGenTypeEnum(codeGenTypeEnum)
            .appId(appId)
            .build();

    return switch (codeGenTypeEnum) {
      case HTML -> htmlFileSaver.save(saverDto);
      case HTML_MULTI_FILE -> htmlMutiFileSaver.save(saverDto);
      default -> throw new IllegalArgumentException("不支持的代码生成类型: " + codeGenTypeEnum);
    };
  }
}
