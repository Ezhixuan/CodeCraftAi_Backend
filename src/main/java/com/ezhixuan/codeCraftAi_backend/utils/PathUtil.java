package com.ezhixuan.codeCraftAi_backend.utils;

import java.io.File;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;


public class PathUtil {

    public static final String TEMP_DIR = ResourceUtil.getResource("static") + "/temp/codeOutput";
    public static final String DEPLOY_DIR = ResourceUtil.getResource("static") + "/temp/deploy";

    public static String buildPath(String basePath, CodeGenTypeEnum codeGenTypeEnum, Object uniqueId) {
        String dirName = StrUtil.format("{}_{}", codeGenTypeEnum.getValue(), uniqueId.toString());
        String path = basePath + File.separator + dirName;
        FileUtil.mkdir(path);
        return path;
    }
}
