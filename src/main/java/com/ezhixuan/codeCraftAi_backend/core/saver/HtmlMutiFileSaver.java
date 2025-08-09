package com.ezhixuan.codeCraftAi_backend.core.saver;

import java.io.File;

import org.springframework.util.StringUtils;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HtmlMutiFileSaver extends FileSaverTemplate<AiChatHtmlCssScriptResDto> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML_MULTI_FILE;
    }

    @Override
    protected void doSave(AiChatHtmlCssScriptResDto resDto, String baseDirPath) {
        saveHtml(resDto.getHtmlCode(), baseDirPath);
        saveCss(resDto.getCssCode(), baseDirPath);
        saveScript(resDto.getScriptCode(), baseDirPath);
    }

    /**
     * 保存文件到 index.html
     *
     * @author Ezhixuan
     * @param basePath 基础路径
     * @param html html 内容
     */
    private void saveHtml(String html, String basePath) {
        if (!StringUtils.hasText(html)) {
            return;
        }
        FileUtil.writeUtf8String(html, basePath + File.separator + "index.html");
        log.info("保存文件到 {}/index.html", basePath);
    }

    /**
     * 保存文件到 style.css
     *
     * @author Ezhixuan
     * @param basePath 基础路径
     * @param css css 内容
     */
    private void saveCss(String css, String basePath) {
        if (!StringUtils.hasText(css)) {
            return;
        }
        FileUtil.writeUtf8String(css, basePath + File.separator + "style.css");
        log.info("保存文件到 {}/style.css", basePath);
    }

    /**
     * 保存文件到 script.js
     *
     * @author Ezhixuan
     * @param basePath 基础路径
     * @param script script 内容
     */
    private void saveScript(String script, String basePath) {
        if (!StringUtils.hasText(script)) {
            return;
        }
        FileUtil.writeUtf8String(script, basePath + File.separator + "script.js");
        log.info("保存文件到 {}/script.js", basePath);
    }
}
