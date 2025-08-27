package com.ezhixuan.codeCraftAi_backend.core.saver;

import static org.springframework.util.StringUtils.hasText;

import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HtmlFileSaver extends FileSaverTemplate<AiChatHtmlResDto> {
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void doSave(AiChatHtmlResDto resDto, String baseDirPath) {
        FileUtil.writeUtf8String(resDto.getHtmlCode(), baseDirPath);
        log.info("保存文件成功: {}", baseDirPath);
    }

    @Override
    protected void validate(AiChatHtmlResDto resDto) {
        super.validate(resDto);
        if (!hasText(resDto.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "html 内容不能为空");
        }
    }

}
