package com.ezhixuan.codeCraftAi_backend.core.saver;

import java.io.File;
import java.util.Objects;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

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
        String baseDirPath = getBasePath(getCodeType(), appId);
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

    /**
     * 获取基础路径
     *
     * @author Ezhixuan
     * @param codeType 代码类型
     * @return java.lang.String
     */
    private String getBasePath(CodeGenTypeEnum codeType, Long appId) {
        String dirName = StrUtil.format("{}_{}", codeType.getValue(), appId);
        String path = TEMP_DIR + File.separator + dirName;
        FileUtil.mkdir(path);
        return path;
    }
}
