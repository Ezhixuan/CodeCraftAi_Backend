package com.ezhixuan.codeCraftAi_backend.core.saver;

import java.io.File;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileSaverExecutor {

    private final HtmlFileSaver htmlFileSaver = new HtmlFileSaver();
    private final HtmlMutiFileSaver htmlMutiFileSaver = new HtmlMutiFileSaver();

    /**
     * 对 ai 返回 code 进行临时保存
     * @author Ezhixuan
     * @param resDto ai 响应结果 like {@link AiChatHtmlResDto} or {@link AiChatHtmlCssScriptResDto}
     * @param codeGenTypeEnum 代码生成类型
     * @return File 临时保存的文件
     */
    public File executeSave(Object resDto, CodeGenTypeEnum codeGenTypeEnum) {
        return switch (codeGenTypeEnum) {
            case HTML -> htmlFileSaver.save((AiChatHtmlResDto) resDto);
            case HTML_MULTI_FILE -> htmlMutiFileSaver.save((AiChatHtmlCssScriptResDto) resDto);
            default -> throw new IllegalArgumentException("不支持的代码生成类型: " + codeGenTypeEnum);
        };
    }


}
