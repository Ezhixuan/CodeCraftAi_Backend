package com.ezhixuan.codeCraftAi_backend.core.saver;

import java.io.File;
import java.util.Objects;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.codeCraftAi_backend.utils.PathUtil;

import cn.hutool.core.io.FileUtil;

public abstract class FileSaverTemplate<T> {

    private static final String TEMP_DIR = System.getProperty("user.dir") + "/temp/codeOutput";

    /**
     * 进行文件临时存储
     * @author Ezhixuan
     * @param resDto ai响应结果
     * @return File
     */
    public final File save(FileSaverDto saverDto) {
        T aiChatDto = (T) saverDto.getAiChatDto();
        Long appId = saverDto.getAppId();

        validate(aiChatDto);
        String baseDirPath = PathUtil.buildPath(PathUtil.ORIGINAL_DIR, getCodeType(), appId);
        doSave(aiChatDto, baseDirPath);
        return FileUtil.file(baseDirPath);
    }

    protected abstract CodeGenTypeEnum getCodeType();

    protected abstract void doSave(T resDto, String baseDirPath);

    protected void validate(T resDto) {
        if (Objects.isNull(resDto)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "未接收到结果");
        }
    }
}
