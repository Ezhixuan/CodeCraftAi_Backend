package com.ezhixuan.codeCraftAi_backend.domain.constant;

import com.ezhixuan.codeCraftAi_backend.ai.tools.ToolEnum;

public interface PromptConstant {

    String GENERATE_APP_NAME = " 在这一次对话中你需要调用" + ToolEnum.APP_NAME_TOOL.getText();
}
