package com.ezhixuan.codeCraftAi_backend.service;

import com.ezhixuan.codeCraftAi_backend.controller.app.vo.*;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * 应用服务接口 提供应用相关的业务逻辑处理接口，包括代码生成、应用管理、预览部署等功能
 *
 * @author Ezhixuan
 * @since 0.0.1beta
 * @version 0.0.3beta
 */
public interface SysAppService extends IService<SysApp> {

  /**
   * 代码生成 根据用户输入生成代码，并以SSE流式方式返回生成过程和结果
   *
   * @param message 用户输入的生成需求描述
   * @param appId 应用ID
   * @return Flux<ServerSentEvent<String>> SSE流式返回生成过程和结果
   */
  Flux<ServerSentEvent<String>> generateCode(String message, Long appId);

  /**
   * 通过用户输入内容生成记录 根据用户输入创建新的应用生成记录
   *
   * @param reqVo 请求体，携带初始化提示信息
   * @return Long 生成的应用ID
   */
  Long doGenerate(AppGenerateReqVo reqVo);

  /**
   * 获取用户应用列表 根据查询条件获取用户的应用列表
   *
   * @param queryReqVo 查询请求体，包含查询条件
   * @param limit 是否限制查询结果
   * @return Page<SysApp> 应用分页列表
   */
  Page<SysApp> getList(AppQueryReqVo queryReqVo, boolean limit);

  /**
   * 转换为通用返回格式 将应用分页列表转换为通用返回格式
   *
   * @param sysAppPage getList返回的应用分页列表
   * @param userInfoMap 用户信息映射表
   * @return Page<AppInfoCommonResVo> 通用返回格式的应用分页列表
   */
  Page<AppInfoCommonResVo> convert2Common(
      Page<SysApp> sysAppPage, Map<Long, UserInfoCommonResVo> userInfoMap);

  /**
   * 转换为管理端返回格式 将应用分页列表转换为管理端返回格式
   *
   * @param sysAppPage getList返回的应用分页列表
   * @param userInfoMap 用户信息映射表
   * @return Page<AppInfoAdminResVo> 管理端返回格式的应用分页列表
   */
  Page<AppInfoAdminResVo> convert2Admin(
      Page<SysApp> sysAppPage, Map<Long, UserInfoAdminResVo> userInfoMap);

  /**
   * 通过部署标识获取应用 根据部署标识查找对应的应用信息
   *
   * @param deployKey 部署标识
   * @return SysApp 应用信息
   */
  SysApp getByDeployKey(String deployKey);

  /**
   * 将文件复制到预览文件夹 将生成的代码文件复制到预览文件夹，用于预览展示
   *
   * @since 0.0.2beta
   * @param appId 应用ID
   * @param reBuild 是否重新构建，只有应用用户为当前用户才可以进行
   * @return String 预览标识
   */
  String copyToPreview(Long appId, boolean reBuild);

  /**
   * 获取应用状态 根据应用ID获取应用的状态信息
   *
   * @since 0.0.2beta
   * @param appId 应用ID
   * @return AppStatusResVo 应用状态信息
   */
  AppStatusResVo getStatus(Long appId);

  /**
   * 执行应用部署 根据应用ID执行应用的部署操作
   *
   * @param appId 应用ID
   * @since 0.0.2beta
   * @return String 访问路径
   */
  String doDeploy(Long appId);

  /**
   * 执行应用代码打包 将应用代码打包成ZIP文件并提供下载
   *
   * @param appId 应用ID
   * @param response HTTP响应对象，用于返回ZIP文件下载流
   * @since 0.0.3beta
   */
  void doZip(Long appId, HttpServletResponse response);

  /**
   * 获取预览或部署的URL地址 根据预览标识和是否为本地环境返回对应的URL地址
   *
   * @since 0.0.3beta
   * @param previewKey 预览标识
   * @param hasLocal 是否为本地环境
   * @return String URL地址
   */
  String getUrl(String previewKey, boolean hasLocal);
}
