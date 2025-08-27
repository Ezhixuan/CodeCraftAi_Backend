package com.ezhixuan.codeCraftAi_backend.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * NPM工具类，用于在不同操作系统上执行npm命令 支持Windows和Linux/Mac系统
 *
 * @author Ezhixuan
 */
@Slf4j
public class NpmUtil {

  private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
  private static final String LINUX_NPM = "npm";
  private static final String WINDOWS_NPM = "npm.cmd";

  /**
   * 执行npm install命令安装依赖
   *
   * @param runPath 命令执行路径
   * @return 执行结果，true表示成功，false表示失败
   */
  public static boolean install(String runPath) {
    if (checkFileIsExist(runPath, "package-lock.json")
        && checkFileIsExist(runPath, "package.json")) {
      log.error("package-lock.json 和 package.json 文件不存在");
      return false;
    }
    String install = String.format("%s i", NPM());
    return run(runPath, install, 5, TimeUnit.MINUTES);
  }

  /**
   * 执行npm run build命令构建项目
   *
   * @param runPath 命令执行路径
   * @return 执行结果，true表示成功，false表示失败
   */
  public static boolean build(String runPath) {
    if (checkFileIsExist(runPath, "package.json")) {
      log.error("package.json 文件不存在");
      return false;
    }
    String build = String.format("%s run build", NPM());
    return run(runPath, build, 3, TimeUnit.MINUTES);
  }

  /**
   * 执行npm命令
   *
   * @param runPath 命令执行路径
   * @param command 要执行的命令
   * @param timeout 超时时间
   * @param unit 超时时间单位
   * @return 执行结果，true表示成功，false表示失败
   */
  public static boolean run(String runPath, String command, long timeout, TimeUnit unit) {
    try {
      log.info("{} 执行 {} 命令", runPath, checkCommand(command));
      // 修复权限问题，使用shell执行命令
      Process exec = RuntimeUtil.exec(null, FileUtil.file(runPath), command);
      boolean waited = exec.waitFor(timeout, unit);
      // 超时销毁
      if (!waited) {
        log.error("执行超时");
        exec.destroyForcibly();
        return false;
      }
      return exec.exitValue() == 0;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 检查并转换命令中的npm关键字，确保在不同操作系统上使用正确的npm命令
   *
   * @param command 原始命令
   * @return 转换后的命令
   */
  private static String checkCommand(String command) {
    if (!command.contains(LINUX_NPM)) return String.format("%s %s", NPM(), command);

    String[] split = command.split(" ");
    for (int i = 0; i < split.length; i++) {
      // 替换npm
      if (Objects.equals(split[i], LINUX_NPM) || Objects.equals(split[i], WINDOWS_NPM)) {
        split[i] = NPM();
      }
    }
    return String.join(" ", split);
  }

  private static boolean checkFileIsExist(String dir, String fileName) {
    File dirfile = FileUtil.file(dir);
    File file = FileUtil.file(dirfile, fileName);
    return !file.exists();
  }

  /**
   * 获取当前操作系统对应的npm命令
   *
   * @return npm命令名称
   */
  private static String NPM() {
    return isWindows() ? WINDOWS_NPM : LINUX_NPM;
  }

  /**
   * 判断当前操作系统是否为Windows
   *
   * @return true表示Windows系统，false表示其他系统
   */
  private static boolean isWindows() {
    return OS_NAME.contains("win");
  }
}
