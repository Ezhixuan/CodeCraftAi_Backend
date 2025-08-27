package com.ezhixuan.codeCraftAi_backend.core.saver;

import cn.hutool.core.io.FileUtil;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * HTML多文件保存器 专门用于保存HTML、CSS和JavaScript代码到独立文件的实现类 继承自文件保存模板类，实现多文件代码的保存逻辑
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Slf4j
public class HtmlMutiFileSaver extends FileSaverTemplate<AiChatHtmlCssScriptResDto> {

  /**
   * 获取代码类型 返回HTML多文件代码生成类型枚举
   *
   * @return HTML多文件代码生成类型枚举
   */
  @Override
  protected CodeGenTypeEnum getCodeType() {
    return CodeGenTypeEnum.HTML_MULTI_FILE;
  }

  /**
   * 执行多文件代码保存操作 分别保存HTML、CSS和JavaScript代码到对应的文件中
   *
   * @param resDto HTML+CSS+JS聊天结果对象，包含各类代码内容
   * @param baseDirPath 基础目录路径，用于保存文件
   */
  @Override
  protected void doSave(AiChatHtmlCssScriptResDto resDto, String baseDirPath) {
    saveHtml(resDto.getHtmlCode(), baseDirPath);
    saveCss(resDto.getCssCode(), baseDirPath);
    saveScript(resDto.getScriptCode(), baseDirPath);
  }

  /**
   * 保存HTML代码到文件 将HTML代码保存为index.html文件
   *
   * @param html HTML代码内容
   * @param basePath 基础路径
   */
  private void saveHtml(String html, String basePath) {
    if (!StringUtils.hasText(html)) {
      return;
    }
    FileUtil.writeUtf8String(html, basePath + File.separator + "index.html");
    log.info("保存文件到 {}/index.html", basePath);
  }

  /**
   * 保存CSS代码到文件 将CSS代码保存为style.css文件
   *
   * @param css CSS代码内容
   * @param basePath 基础路径
   */
  private void saveCss(String css, String basePath) {
    if (!StringUtils.hasText(css)) {
      return;
    }
    FileUtil.writeUtf8String(css, basePath + File.separator + "style.css");
    log.info("保存文件到 {}/style.css", basePath);
  }

  /**
   * 保存JavaScript代码到文件 将JavaScript代码保存为script.js文件
   *
   * @param script JavaScript代码内容
   * @param basePath 基础路径
   */
  private void saveScript(String script, String basePath) {
    if (!StringUtils.hasText(script)) {
      return;
    }
    FileUtil.writeUtf8String(script, basePath + File.separator + "script.js");
    log.info("保存文件到 {}/script.js", basePath);
  }
}
