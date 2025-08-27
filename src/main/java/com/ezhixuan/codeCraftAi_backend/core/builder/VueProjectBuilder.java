package com.ezhixuan.codeCraftAi_backend.core.builder;

import cn.hutool.core.io.FileUtil;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.utils.NpmUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Vue项目构建器 用于构建Vue项目的npm依赖安装和项目打包
 *
 * @version 0.0.1beta
 * @author Ezhixuan
 */
@Slf4j
public class VueProjectBuilder implements ProjectBuilder {

  /**
   * 构建Vue项目，包括安装依赖和打包构建
   *
   * @param buildPath 项目路径
   * @param async 是否异步执行
   * @return 构建结果，同步模式下返回实际构建结果，异步模式下始终返回true
   */
  @Override
  public boolean build(String buildPath, boolean async) {
    try {
      // 判断文件路径是否存在
      boolean directory = FileUtil.isDirectory(buildPath);
      if (!directory) {
        log.error("文件路径不存在");
        return false;
      }

      if (async) {
        // 异步执行构建任务
        String virtualName =
            String.format(
                "%s_i&b_%s", CodeGenTypeEnum.VUE_PROJECT.getValue(), System.currentTimeMillis());
        Thread.ofVirtual()
            .name(virtualName)
            .start(
                () -> {
                  boolean install = NpmUtil.install(buildPath);
                  if (install) {
                    NpmUtil.build(buildPath);
                  }
                });
        // 异步模式下直接返回true
        return true;
      } else {
        // 同步执行构建任务
        boolean install = NpmUtil.install(buildPath);
        if (install) {
          return NpmUtil.build(buildPath);
        }
        return false;
      }
    } catch (Exception e) {
      log.error("vue 项目构建失败, {}", e.getMessage(), e);
      return false;
    }
  }
}
