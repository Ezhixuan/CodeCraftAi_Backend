package com.ezhixuan.codeCraftAi_backend.service;

import com.ezhixuan.codeCraftAi_backend.controller.deploy.vo.DeployStatusVo;

import jakarta.servlet.http.HttpServletResponse;

public interface IDeployService {

    /**
     * 对应用内容进行预部署提供查看
     * @author Ezhixuan
     * @param appId 应用ID
     * @return String 部署 key
     */
    String preDeploy(Long appId);

    /**
     * 通过部署 key 重定向到静态资源
     * @author Ezhixuan
     * @param deployKey 部署 key
     * @param response 响应
     */
    void redirectToStaticResource(String deployKey, HttpServletResponse response);

    /**
     * 获取应用部署状态
     * @author Ezhixuan
     * @param appId 应用ID
     * @return DeployStatusVo 应用部署状态
     */
    DeployStatusVo getDeployStatus(Long appId);
}
