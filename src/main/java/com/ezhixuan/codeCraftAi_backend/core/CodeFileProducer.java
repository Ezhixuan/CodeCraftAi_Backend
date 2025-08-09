package com.ezhixuan.codeCraftAi_backend.core;

import java.io.File;

import org.springframework.util.StringUtils;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于将模型生成的代码文件转换为文件
 */
@UtilityClass
@Slf4j
public class CodeFileProducer {

    public String TEMP_DIR = System.getProperty("user.dir") + "/temp/codeOutput";

    public File produce(AiChatHtmlResDto htmlResDto) {
        return produce(htmlResDto.getHtmlCode(), null, null, CodeGenTypeEnum.HTML);

    }

    public File produce(AiChatHtmlCssScriptResDto htmlCssScriptResDto) {
        return produce(htmlCssScriptResDto.getHtmlCode(), htmlCssScriptResDto.getCssCode(), htmlCssScriptResDto.getScriptCode(),
            CodeGenTypeEnum.HTML_MULTI_FILE);

    }

    private File produce(String html, String css, String script, CodeGenTypeEnum codeType) {
        String basePath = getBasePath(codeType);
        saveHtml(basePath, html);
        saveCss(basePath, css);
        saveScript(basePath, script);
        return FileUtil.file(basePath);
    }

    /**
     * 获取基础路径
     *
     * @author Ezhixuan
     * @param codeType 代码类型
     * @return java.lang.String
     */
    private String getBasePath(CodeGenTypeEnum codeType) {
        String dirName = StrUtil.format("{}_{}", codeType.getValue(), IdUtil.getSnowflakeNextId());
        String path = TEMP_DIR + File.separator + dirName;
        FileUtil.mkdir(path);
        return path;
    }

    /**
     * 保存文件到 index.html
     *
     * @author Ezhixuan
     * @param basePath 基础路径
     * @param html html 内容
     */
    private void saveHtml(String basePath, String html) {
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
    private void saveCss(String basePath, String css) {
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
    private void saveScript(String basePath, String script) {
        if (!StringUtils.hasText(script)) {
            return;
        }
        FileUtil.writeUtf8String(script, basePath + File.separator + "script.js");
        log.info("保存文件到 {}/script.js", basePath);
    }

}
