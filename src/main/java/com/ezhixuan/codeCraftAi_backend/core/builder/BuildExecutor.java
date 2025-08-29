package com.ezhixuan.codeCraftAi_backend.core.builder;

import cn.hutool.core.io.FileUtil;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.utils.PathUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 构建任务执行器 使用执行器模式处理不同类型的项目构建任务
 *
 * @version 0.0.1beta
 * @author Ezhixuan
 */
@Slf4j
public class BuildExecutor {

  private static final VueProjectBuilder VUE_PROJECT_BUILDER = new VueProjectBuilder();

  /**
   * 根据应用ID执行构建任务 <br>
   * 固定构建路径为 tempPath, 如果该文件已存在会进行一次清理 <br>
   * 将 originalPath 的内容复制到 targetPath
   *
   *
   * @param appId 应用ID，用于标识特定的应用
   * @param codeGenTypeEnum 代码生成类型枚举，决定使用哪种构建方式
   * @param async 是否异步执行构建任务，true表示异步，false表示同步
   * @return 构建结果，true表示构建成功，false表示构建失败
   */
  public static boolean build(Long appId, CodeGenTypeEnum codeGenTypeEnum, boolean async) {
    log.info("开始执行构建任务，应用ID: {}, 代码生成类型: {}, 异步: {}", appId, codeGenTypeEnum, async);
    String targetPath = PathUtil.buildPath(PathUtil.TEMP_DIR, codeGenTypeEnum, appId);
    if (FileUtil.exist(targetPath)) {
      FileUtil.clean(targetPath);
    }
    String originalPath = PathUtil.buildPath(PathUtil.ORIGINAL_DIR, codeGenTypeEnum, appId);
    FileUtil.copyContent(FileUtil.file(originalPath), FileUtil.file(targetPath), true);
    return switch (codeGenTypeEnum) {
      case HTML, HTML_MULTI_FILE -> true;
      case VUE_PROJECT -> VUE_PROJECT_BUILDER.build(targetPath, async);
    };
  }

  /**
   * 根据构建路径执行构建任务
   *
   * @param buildPath 构建路径，指定项目构建的目标目录
   * @param codeGenTypeEnum 代码生成类型枚举，决定使用哪种构建方式
   * @param async 是否异步执行构建任务，true表示异步，false表示同步
   * @return 构建结果，true表示构建成功，false表示构建失败
   */
  public static boolean build(String buildPath, CodeGenTypeEnum codeGenTypeEnum, boolean async) {
    log.info("开始执行构建任务，构建路径: {}, 代码生成类型: {}, 异步: {}", buildPath, codeGenTypeEnum, async);
    return switch (codeGenTypeEnum) {
      case HTML, HTML_MULTI_FILE -> true;
      case VUE_PROJECT -> VUE_PROJECT_BUILDER.build(buildPath, async);
    };
  }
}
