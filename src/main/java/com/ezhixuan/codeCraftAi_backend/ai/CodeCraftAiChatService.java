package com.ezhixuan.codeCraftAi_backend.ai;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatBaseResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlCssScriptResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;

import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

public interface CodeCraftAiChatService {

    /**
     * 返回基础对话信息
     * @author Ezhixuan
     * @param userMessage 用户信息
     * @return AiChatBaseResponse 基础对话信息
     */
    @SystemMessage(fromResource = "prompt/codeCraft-common-generate-system-prompt.txt")
    AiChatBaseResDto chat(String userMessage);

    /**
     * 获取html信息
     * @author Ezhixuan
     * @param userMessage 用户信息
     * @return AiChatHtmlResDto html信息
     */
    @SystemMessage(fromResource = "prompt/codeCraft-html-generate-system-prompt.txt")
    AiChatHtmlResDto chatHtml(String userMessage);

    /**
     * 获取html信息
     * @author Ezhixuan
     * @param userMessage 用户信息
     * @return AiChatHtmlCssScriptResDto html信息
     */
    @SystemMessage(fromResource = "prompt/codeCraft-htmlAcssAjs-generate-system-prompt.txt")
    AiChatHtmlCssScriptResDto chatHtmlCssScript(String userMessage);

    /**
     * 获取html信息 (带流式返回)
     *
     * @author Ezhixuan
     * @param userMessage 用户信息
     * @return AiChatHtmlCssScriptResDto html信息
     */
    @SystemMessage(fromResource = "prompt/codeCraft-common-generate-system-prompt.txt")
    Flux<String> chatStream(String userMessage);

    /**
     * 获取html信息 (带流式返回)
     *
     * @author Ezhixuan
     * @param userMessage 用户信息
     * @return AiChatHtmlCssScriptResDto html信息
     */
    @SystemMessage(fromResource = "prompt/codeCraft-html-generate-system-prompt.txt")
    Flux<String> chatHtmlStream(String userMessage);

    /**
     * 获取html信息 (带流式返回)
     *
     * @author Ezhixuan
     * @param userMessage 用户信息
     * @return AiChatHtmlCssScriptResDto html信息
     */
    @SystemMessage(fromResource = "prompt/codeCraft-htmlAcssAjs-generate-system-prompt.txt")
    Flux<String> chatHtmlCssScriptStream(String userMessage);
}
