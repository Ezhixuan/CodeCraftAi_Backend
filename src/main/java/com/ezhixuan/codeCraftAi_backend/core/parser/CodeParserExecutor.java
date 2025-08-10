package com.ezhixuan.codeCraftAi_backend.core.parser;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CodeParserExecutor {

    private final CodeParser<AiChatHtmlResDto> HTML_CODE_PARSER = new HtmlCodeParser();
    private final CodeParser<AiChatHtmlCssScriptResDto> HTML_CSS_SCRIPT_PARSER = new HtmlMutiCodeParser();

    /**
     * 代码解析执行器
     * @author Ezhixuan
     * @param content 待解析的内容
     * @param codeGenTypeEnum 代码生成类型枚举
     * @return 解析结果 like {@link AiChatHtmlResDto} or {@link AiChatHtmlCssScriptResDto}
     */
    public Object executeParse(String content, CodeGenTypeEnum codeGenTypeEnum) {
        return switch (codeGenTypeEnum) {
            case HTML -> HTML_CODE_PARSER.parse(content);
            case HTML_MULTI_FILE -> HTML_CSS_SCRIPT_PARSER.parse(content);
            default -> throw new IllegalArgumentException("不支持的代码生成类型: " + codeGenTypeEnum);
        };
    }

}
