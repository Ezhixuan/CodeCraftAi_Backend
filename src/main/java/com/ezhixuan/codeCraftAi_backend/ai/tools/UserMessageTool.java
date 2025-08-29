package com.ezhixuan.codeCraftAi_backend.ai.tools;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.ezhixuan.codeCraftAi_backend.service.SysAppService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 用户消息处理工具类
 * 提供处理用户消息相关的方法
 * @author ezhixuan
 * @version 0.0.2beta
 */
@Slf4j
@Component
public class UserMessageTool {

  @Resource @Lazy private SysAppService appService;

  /**
   * 工具执行结果格式化方法
   * 将工具执行的结果格式化为可读的字符串格式，包含应用名称和执行结果
   *
   * @since 0.0.2beta
   * @param toolExecution 工具执行对象，包含请求参数和执行结果
   * @return 格式化后的执行结果字符串
   */
  public static String generateAppNameToolRes(ToolExecution toolExecution) {
    JSONObject jsonObject = JSONUtil.parseObj(toolExecution.request().arguments());
    String appName = jsonObject.getStr("appName");
    String res = toolExecution.result();
    ToolEnum appNameTool = ToolEnum.APP_NAME_TOOL;
    String result =
        String.format(
            """
            [%s] %s
            应用名称: %s
            """,
            appNameTool.getText(), res, appName);
    return String.format("\n\n%s\n\n", result);
  }

  /**
   * 根据用户消息生成应用名称并更新应用信息
   * 将用户消息概括为12个中文字符内的应用名称，并更新对应应用的名称信息
   *
   * @param appName 概括出的应用名，限制在12个中文字符内
   * @param appId 应用的唯一标识ID
   * @return 操作结果信息，成功时返回"生成应用名成功"加应用名，失败时返回"生成应用名失败"
   */
  @Tool("将用户消息进行概括,概括为 12 个中文字符内的 appName,必须且只能调用一次")
  public String generateAppNameTool(@P("概括出的应用名 12 个中文字符内") String appName, @ToolMemoryId long appId) {
    try {
      SysApp sysApp = new SysApp();
      sysApp.setId(appId);
      sysApp.setName(appName);
      appService.updateById(sysApp);
      return "生成应用名成功" + appName;
    } catch (Exception e) {
      log.error("生成应用名失败", e);
      return "生成应用名失败";
    }
  }
}
