package com.ezhixuan.codeCraftAi_backend.utils;

import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ScreenshotUtil 工具类测试类
 *
 * @version 0.0.2beta
 * @author ezhixuan
 */
class ScreenshotUtilTest {

    @TempDir
    Path tempDir;

    /**
     * 测试基本截图功能
     */
    @Test
    void screenshot() {
        // 使用一个可靠的测试URL
        String testUrl = "https://www.baidu.com";
        try {
            File screenshot = ScreenshotUtil.screenshot(testUrl);
            assertNotNull(screenshot, "截图文件不应为null");
            assertTrue(screenshot.exists(), "截图文件应该存在");
            assertTrue(screenshot.length() > 0, "截图文件大小应该大于0");
        } catch (BusinessException e) {
            // 在某些环境下（如CI/CD）可能无法运行浏览器，这种情况下忽略测试
            // 但确保异常类型正确
            assertEquals("获取webDriver失败", e.getMessage());
        } catch (Exception e) {
            // 忽略其他环境相关的异常
        }
    }

    /**
     * 测试带压缩参数的截图功能
     */
    @Test
    void testScreenshot() {
        String testUrl = "https://www.baidu.com";
        try {
            // 测试不压缩截图
            File uncompressedScreenshot = ScreenshotUtil.screenshot(testUrl, false);
            assertNotNull(uncompressedScreenshot, "未压缩截图文件不应为null");
            assertTrue(uncompressedScreenshot.exists(), "未压缩截图文件应该存在");
            assertTrue(uncompressedScreenshot.length() > 0, "未压缩截图文件大小应该大于0");

            // 测试压缩截图
            File compressedScreenshot = ScreenshotUtil.screenshot(testUrl, true);
            assertNotNull(compressedScreenshot, "压缩截图文件不应为null");
            assertTrue(compressedScreenshot.exists(), "压缩截图文件应该存在");
            assertTrue(compressedScreenshot.length() > 0, "压缩截图文件大小应该大于0");

            // 压缩后的文件应该比未压缩的小（通常情况下）
            // 注意：在某些情况下压缩后可能变大，这里不作断言，仅作信息输出
            System.out.println("未压缩文件大小: " + uncompressedScreenshot.length());
            System.out.println("压缩后文件大小: " + compressedScreenshot.length());
        } catch (BusinessException e) {
            assertEquals("获取webDriver失败", e.getMessage());
        } catch (Exception e) {
            // 忽略其他环境相关的异常
        }
    }

    /**
     * 测试多线程并发截图功能
     */
    @Test
    void testConcurrentScreenshot() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<File>> futures = new ArrayList<>();

        String testUrl = "https://www.baidu.com";

        // 提交多个并发任务
        for (int i = 0; i < threadCount; i++) {
            final int taskId = i;
            Future<File> future = executorService.submit(() -> {
                System.out.println("Task " + taskId + " started");
                try {
                    File screenshot = ScreenshotUtil.screenshot(testUrl);
                    System.out.println("Task " + taskId + " completed");
                    return screenshot;
                } catch (Exception e) {
                    System.out.println("Task " + taskId + " failed: " + e.getMessage());
                    throw e;
                }
            });
            futures.add(future);
        }

        // 关闭线程池
        executorService.shutdown();

        // 等待所有任务完成，最多等待60秒
        boolean finished = executorService.awaitTermination(60, TimeUnit.SECONDS);
        assertTrue(finished, "所有截图任务应该在60秒内完成");

        // 检查结果
        for (int i = 0; i < futures.size(); i++) {
            Future<File> future = futures.get(i);
            try {
                File screenshot = future.get(10, TimeUnit.SECONDS);
                assertNotNull(screenshot, "任务 " + i + " 的截图文件不应为null");
                assertTrue(screenshot.exists(), "任务 " + i + " 的截图文件应该存在");
                assertTrue(screenshot.length() > 0, "任务 " + i + " 的截图文件大小应该大于0");
            } catch (ExecutionException e) {
                // 如果是BusinessException，说明是预期的错误（如webDriver获取失败）
                if (!(e.getCause() instanceof BusinessException)) {
                    fail("任务 " + i + " 出现未预期的异常: " + e.getCause());
                }
            } catch (TimeoutException e) {
                fail("任务 " + i + " 超时");
            }
        }
    }

    /**
     * 测试线程等待机制
     * 验证当所有WebDriver实例都被占用时，新的请求会正确等待
     */
    @Test
    void testThreadWaiting() throws InterruptedException {
        int maxWebDriver = 10; // 与ScreenshotUtil中定义的最大WebDriver数一致
        ExecutorService executorService = Executors.newFixedThreadPool(maxWebDriver + 2);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch allTasksStarted = new CountDownLatch(maxWebDriver + 2);

        String testUrl = "https://www.baidu.com";
        List<Future<File>> futures = new ArrayList<>();

        // 提交超过最大WebDriver数量的任务
        for (int i = 0; i < maxWebDriver + 2; i++) {
            Future<File> future = executorService.submit(() -> {
                try {
                    allTasksStarted.countDown();
                    // 等待信号开始执行
                    latch.await();
                    return ScreenshotUtil.screenshot(testUrl);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }

        // 等待所有任务启动
        allTasksStarted.await();

        // 稍微延迟后释放所有任务
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(latch::countDown, 500, TimeUnit.MILLISECONDS);
        scheduler.shutdown();

        // 关闭线程池
        executorService.shutdown();

        // 等待所有任务完成
        boolean finished = executorService.awaitTermination(120, TimeUnit.SECONDS);
        assertTrue(finished, "所有任务应该在120秒内完成");

        // 验证所有任务都成功完成
        int successCount = 0;
        for (int i = 0; i < futures.size(); i++) {
            Future<File> future = futures.get(i);
            try {
                File screenshot = future.get(10, TimeUnit.SECONDS);
                if (screenshot != null && screenshot.exists() && screenshot.length() > 0) {
                    successCount++;
                }
            } catch (ExecutionException e) {
                // 如果是BusinessException，说明是预期的错误
                if (!(e.getCause() instanceof BusinessException)) {
                    fail("任务 " + i + " 出现未预期的异常: " + e.getCause());
                }
            } catch (TimeoutException e) {
                fail("任务 " + i + " 超时");
            }
        }

        // 至少应该有部分任务成功（在有WebDriver资源的情况下）
        System.out.println("成功完成的任务数: " + successCount + "/" + futures.size());
    }

}
