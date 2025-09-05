package com.ezhixuan.codeCraftAi_backend.domain.constant;

import com.ezhixuan.codeCraftAi_backend.ai.tools.ToolEnum;

public interface PromptConstant {

    String GENERATE_APP_NAME = " 在这一次对话中你需要调用" + ToolEnum.APP_NAME_TOOL.getText();

    String SELECTED_ELEMENT = " 根据所选元素的路由及文件信息，定位对应页面路径，并按要求修改元素";
}
