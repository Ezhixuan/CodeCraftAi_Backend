package com.ezhixuan.codeCraftAi_backend.core;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;

import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

@SpringBootTest
class CodeCraftFacadeTest {

    @Resource
    private CodeCraftFacade codeCraftFacade;

    @Test
    void chatAndSave() {
        File file = codeCraftFacade.chatAndSave("请生成一个现代化风格的登录页面”", CodeGenTypeEnum.HTML_MULTI_FILE);
        assert file != null;
    }

    @Test
    void chatAndSaveStream() {
        Flux<String> stringFlux = codeCraftFacade.chatAndSaveStream("帮我生成一个任务清单页面,要求能够输入内容回车直接记录下任务,并且提供有序的任务列表,可以通过按钮表示任务完成,完成后进入完成列表并记录完成时间", CodeGenTypeEnum.HTML_MULTI_FILE);
        List<String> blocked = stringFlux.collectList().block();
        assert blocked != null;
        String joined = String.join("", blocked);
        System.out.println(joined);

    }
}
