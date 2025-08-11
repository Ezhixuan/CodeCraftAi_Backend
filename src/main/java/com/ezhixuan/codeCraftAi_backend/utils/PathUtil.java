package com.ezhixuan.codeCraftAi_backend.utils;

import java.io.File;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathUtil {

    public final String TEMP_DIR = ResourceUtil.getResource("static") + "/temp/codeOutput";
    public final String DEPLOY_DIR = ResourceUtil.getResource("static") + "/temp/deploy";

    public String buildPath(String basePath, CodeGenTypeEnum codeGenTypeEnum, Object uniqueId) {
        String dirName = StrUtil.format("{}_{}", codeGenTypeEnum.getValue(), uniqueId.toString());
        String path = basePath + File.separator + dirName;
        FileUtil.mkdir(path);
        return path;
    }
}
