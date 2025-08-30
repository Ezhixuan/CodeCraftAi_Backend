package com.ezhixuan.codeCraftAi_backend.utils;

import cn.hutool.core.img.ImgUtil;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 截图工具类 提供网页截图功能，使用WebDriver池化管理提高性能
 *
 * @version 0.0.2beta
 * @author ezhixuan
 */
@Slf4j
public class ScreenshotUtil {

  /** 最大WebDriver实例数 */
  private static final int MAX_WEB_DRIVER = 10;
  private static final int WIDTH = 1920;
  private static final int HEIGHT = 1080;

  /** WebDriver实例阻塞队列，用于池化管理 */
  private static final BlockingQueue<WebDriver> WEB_DRIVERS =
      new ArrayBlockingQueue<>(MAX_WEB_DRIVER);

  static {
    try {
      WebDriverManager.chromedriver().setup();

      for (int i = 0; i < MAX_WEB_DRIVER; i++) {
        WEB_DRIVERS.offer(createWebDriver(WIDTH, HEIGHT));
      }
    } catch (Exception exception) {
      log.error("webDriver初始化失败", exception);
    }
  }

  /**
   * 对指定URL网页进行截图
   *
   * @param webUrl 网页URL
   * @return 截图文件
   * @throws BusinessException 获取WebDriver失败时抛出
   */
  public static File screenshot(String webUrl) {
    return screenshot(webUrl, false);
  }

  /**
   * 对指定URL网页进行截图
   *
   * @param webUrl 网页URL
   * @param compressed 是否压缩截图
   * @return 截图文件
   * @throws BusinessException 获取WebDriver失败时抛出
   */
  public static File screenshot(String webUrl, boolean compressed) {
    WebDriver webDriver = null;
    try {
      webDriver = WEB_DRIVERS.take();
      if (!isHealthyDriver(webDriver)) {
        webDriver.quit();
        webDriver = createWebDriver(WIDTH, HEIGHT);
      }
      webDriver.get(webUrl);
      waitPageLoad(webDriver);
      File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
      String name = screenshot.getName();
      System.out.println(name);
      return compressed ? compress(screenshot) : screenshot;
    } catch (Exception exception) {
      log.error("获取webDriver失败", exception);
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取webDriver失败");
    } finally {
      Optional.ofNullable(webDriver).ifPresent(WEB_DRIVERS::offer);
    }
  }

  /**
   * 压缩截图文件
   *
   * @param screenshot 原始截图文件
   * @return 压缩后的截图文件
   * @throws BusinessException 压缩图片失败时抛出
   */
  private static File compress(File screenshot) {
    final float COMPRESS_QUALITY = 0.3f;
    try {
      File compressedFile = File.createTempFile(System.currentTimeMillis() + "_compressed", ".jpg");
      ImgUtil.compress(screenshot, compressedFile, COMPRESS_QUALITY);
      return compressedFile;
    } catch (Exception exception) {
      log.error("压缩图片失败", exception);
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩图片失败");
    }
  }

  /**
   * 等待页面加载完成
   *
   * @param driver WebDriver实例
   */
  private static void waitPageLoad(WebDriver driver) {
    try {
      WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
      // 等待 document.readyState 为 complete
      webDriverWait.until(
          webDriver ->
              Objects.equals(
                  ((JavascriptExecutor) webDriver).executeScript("return document.readyState"),
                  "complete"));
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      log.error("等待页面加载失败", e);
    }
  }

  /**
   * 获取Chrome浏览器选项
   *
   * @param width 浏览器窗口宽度
   * @param height 浏览器窗口高度
   * @return ChromeOptions配置对象
   */
  private static ChromeOptions getChromeOptions(int width, int height) {
    ChromeOptions options = new ChromeOptions();
    // 无头模式
    options.addArguments("--headless");
    // 禁用GPU（在某些环境下避免问题）
    options.addArguments("--disable-gpu");
    // 禁用沙盒模式（Docker环境需要）
    options.addArguments("--no-sandbox");
    // 禁用开发者shm使用
    options.addArguments("--disable-dev-shm-usage");
    // 设置窗口大小
    options.addArguments(String.format("--window-size=%d,%d", width, height));
    // 禁用扩展
    options.addArguments("--disable-extensions");
    // 设置用户代理
    options.addArguments(
        "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
    return options;
  }

  /**
   * 创建WebDriver实例
   *
   * @param width 浏览器窗口宽度
   * @param height 浏览器窗口高度
   * @return WebDriver实例
   * @throws BusinessException 初始化Chrome浏览器失败时抛出
   */
  private static WebDriver createWebDriver(int width, int height) {
    try {
      // 配置 Chrome 选项
      ChromeOptions options = getChromeOptions(width, height);
      // 创建驱动
      WebDriver driver = new ChromeDriver(options);
      // 设置页面加载超时
      driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
      // 设置隐式等待
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
      return driver;
    } catch (Exception e) {
      log.error("初始化 Chrome 浏览器失败", e);
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 Chrome 浏览器失败");
    }
  }

  /**
   * 检查WebDriver实例是否健康可用
   *
   * @param driver 待检查的WebDriver实例
   * @return true表示健康可用，false表示不可用
   */
  private static boolean isHealthyDriver(WebDriver driver) {
    try {
      driver.getCurrentUrl();
      return true;
    } catch (Exception exception) {
      return false;
    }
  }

  /**
   * 销毁方法，用于在应用关闭时退出所有WebDriver实例<br>
   * 通过PreDestroy注解标记，确保在Spring容器关闭时自动调用此方法
   */
  @PreDestroy
  public void destroy() {
    WEB_DRIVERS.forEach(WebDriver::quit);
  }
}
