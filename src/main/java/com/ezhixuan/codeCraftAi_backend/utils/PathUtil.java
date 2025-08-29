package com.ezhixuan.codeCraftAi_backend.utils;

import cn.hutool.core.util.StrUtil;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

/**
 * 路径工具类 用于构建和管理代码生成过程中的临时文件路径和部署路径
 *
 * @author ezhixuan
 * @version 0.0.2beta
 * @since 0.0.1beta
 */
public class PathUtil {

  /** 预览目录路径，用于存储代码生成的预览文件 */
  public static final String PREVIEW_DIR = resourcePath() + File.separator + "preview";

  /** 临时目录路径，用于存储临时文件 */
  public static final String TEMP_DIR = resourcePath() + File.separator + "temp";

  /** 原始目录路径，用于存储代码生成的原始文件 */
  public static final String ORIGINAL_DIR =
      System.getProperty("user.dir") + File.separator + "codeOutput" + File.separator + "original";

  /** 部署目录路径，用于存储准备部署的代码文件 */
  public static final String DEPLOY_DIR =
      System.getProperty("user.dir") + File.separator + "codeOutput" + File.separator + "deploy";

  /**
   * 构建路径方法 根据基础路径、代码生成类型和唯一标识符构建完整的目录路径
   *
   * @param basePath 基础路径
   * @param codeGenTypeEnum 代码生成类型枚举
   * @param uniqueId 唯一标识符
   * @return 构建完成的完整路径
   */
  public static String buildPath(
      String basePath, CodeGenTypeEnum codeGenTypeEnum, Object uniqueId) {
    String dirName = StrUtil.format("{}_{}", codeGenTypeEnum.getValue(), uniqueId.toString());
    return basePath + File.separator + dirName;
  }

  /**
   * 获取项目resources目录的绝对路径 通过Spring的ResourceUtils工具类获取classpath根路径并转换为绝对路径
   *
   * @since 0.0.2beta
   * @return resources目录的绝对路径
   */
  public static String resourcePath() {
    try {
      return ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX).getAbsolutePath();
    } catch (IOException exception) {
      return System.getProperty("user.dir")
          + File.separator
          + "target"
          + File.separator
          + "classes";
    }
  }
}
