package com.ezhixuan.codeCraftAi_backend.service.impl;

import cn.hutool.core.io.FileUtil;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.controller.deploy.vo.DeployStatusVo;
import com.ezhixuan.codeCraftAi_backend.core.builder.BuildExecutor;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.codeCraftAi_backend.service.IDeployService;
import com.ezhixuan.codeCraftAi_backend.service.SysAppService;
import com.ezhixuan.codeCraftAi_backend.utils.DeployUtil;
import com.ezhixuan.codeCraftAi_backend.utils.PathUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Objects;

import static cn.hutool.core.io.FileUtil.*;
import static java.util.Objects.isNull;

@Service
@Slf4j
public class IDeployServiceImpl implements IDeployService {

    @Resource
    private SysAppService appService;

    @Override
    @Deprecated
    public String preDeploy(Long appId) {
        SysApp sysApp = getByAppId(appId);
        String deployKey = getDeployKey(sysApp);

        // 获取来源路径与目标路径
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(sysApp.getCodeGenType());
        if (isNull(codeGenTypeEnum)) {
            log.error("应用id:{}存在代码生成类型错误,请检查 codeGenType:{}", appId, sysApp.getCodeGenType());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码生成类型错误");
        }
        String sourcePath = PathUtil.buildPath(PathUtil.ORIGINAL_DIR, codeGenTypeEnum, appId);
        String deployPath = PathUtil.buildPath(PathUtil.DEPLOY_DIR, codeGenTypeEnum, deployKey);
        File sourceFile = file(sourcePath);
        File deployFile = file(deployPath);

        FileUtil.clean(deployFile);
        copyContent(sourceFile, deployFile, true);
        BuildExecutor.build(deployPath, codeGenTypeEnum, false);
        // 更新部署时间
        sysApp.setDeployTime(LocalDateTime.now());
        appService.updateById(sysApp);
        log.info("应用 {} 预部署成功，路径: {}", appId, deployPath);

        if (Objects.equals(codeGenTypeEnum,CodeGenTypeEnum.VUE_PROJECT)) {
            return deployPath.substring(deployPath.lastIndexOf("/") + 1) + "/dist";
        }
        return deployPath.substring(deployPath.lastIndexOf("/") + 1);
    }

    @Override
    @Deprecated
    @SneakyThrows
    public void redirectToStaticResource(String deployKey, HttpServletResponse response) {
        // 直接检查静态资源文件是否存在
        String deployPath = PathUtil.DEPLOY_DIR + File.separator + deployKey;
        File deployDir = FileUtil.file(deployPath);
        File indexFile = FileUtil.file(deployDir, "index.html");

        if (!indexFile.exists()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "静态资源不存在");
        }

        // 构建重定向URL
        String redirectUrl = "/api/static/" + deployKey + "/index.html";
        response.sendRedirect(redirectUrl);
    }

    @Override
    public DeployStatusVo getDeployStatus(Long appId) {
        SysApp sysApp = getByAppId(appId);

        DeployStatusVo statusVo = new DeployStatusVo();
        statusVo.setAppId(appId);
        statusVo.setDeployTime(sysApp.getDeployTime());

        // 检查临时文件是否存在
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(sysApp.getCodeGenType());
        if (codeGenTypeEnum != null) {
            String tempPath = PathUtil.buildPath(PathUtil.ORIGINAL_DIR, codeGenTypeEnum, appId);
            File tempFile = FileUtil.file(tempPath);
            statusVo.setTempFileExists(tempFile.exists() && tempFile.listFiles() != null && tempFile.listFiles().length > 0);

            // 检查部署文件是否存在
            if (sysApp.getDeployKey() != null) {
                String deployPath = PathUtil.buildPath(PathUtil.DEPLOY_DIR, codeGenTypeEnum, sysApp.getDeployKey());
                File deployFile = FileUtil.file(deployPath);
                statusVo.setDeployFileExists(deployFile.exists() && deployFile.listFiles() != null && deployFile.listFiles().length > 0);
                statusVo.setPreDeployKey(deployPath.substring(deployPath.lastIndexOf("/") + 1));
            } else {
                statusVo.setDeployFileExists(false);
            }
        } else {
            statusVo.setTempFileExists(false);
            statusVo.setDeployFileExists(false);
        }
        return statusVo;
    }

    private SysApp getByAppId(Long appId) {
        // 获取应用信息
        SysApp sysApp = appService.getById(appId);
        if (sysApp == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        return sysApp;
    }

    /**
     * <P>
     * 获取部署密钥.如果不存在则生成密钥并更新数据,如果密钥不合规则重置密钥
     * </P>
     *
     * @author Ezhixuan
     * @param sysApp
     * @return String
     */
    private String getDeployKey(SysApp sysApp) {
        if (isNull(sysApp) || isNull(sysApp.getId())) {
            return DeployUtil.initDeployKey();
        }
        String currDeployKey = sysApp.getDeployKey();
        if (DeployUtil.checkDeployKey(currDeployKey)) {
            String deployKey = DeployUtil.initDeployKey();
            sysApp.setDeployKey(deployKey);
            appService.updateById(sysApp);
            return deployKey;
        }
        return currDeployKey;
    }
}
