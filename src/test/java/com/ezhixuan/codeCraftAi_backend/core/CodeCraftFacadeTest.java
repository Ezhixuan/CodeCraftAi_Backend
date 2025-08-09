package com.ezhixuan.codeCraftAi_backend.core;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;

import jakarta.annotation.Resource;

@SpringBootTest
class CodeCraftFacadeTest {

    @Resource
    private CodeCraftFacade codeCraftFacade;

    @Test
    void chatAndSave() {
        File file = codeCraftFacade.chatAndSave("请生成一个现代化风格的登录页面”", CodeGenTypeEnum.HTML_MULTI_FILE);
        assert file != null;
    }
}
