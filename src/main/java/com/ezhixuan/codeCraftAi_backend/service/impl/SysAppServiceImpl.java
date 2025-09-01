package com.ezhixuan.codeCraftAi_backend.service.impl;

import static com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum.VUE_PROJECT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.common.PageRequest;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.*;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.core.CodeCraftFacade;
import com.ezhixuan.codeCraftAi_backend.core.builder.BuildExecutor;
import com.ezhixuan.codeCraftAi_backend.domain.constant.PromptConstant;
import com.ezhixuan.codeCraftAi_backend.domain.dto.sys.chatHistory.SysChatHistorySubmitDto;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.ezhixuan.codeCraftAi_backend.domain.enums.LoadingStatusEnum;
import com.ezhixuan.codeCraftAi_backend.domain.enums.MessageTypeEnum;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.codeCraftAi_backend.mapper.SysAppMapper;
import com.ezhixuan.codeCraftAi_backend.service.PictureService;
import com.ezhixuan.codeCraftAi_backend.service.SysAppService;
import com.ezhixuan.codeCraftAi_backend.service.SysChatHistoryService;
import com.ezhixuan.codeCraftAi_backend.utils.PathUtil;
import com.ezhixuan.codeCraftAi_backend.utils.ScreenshotUtil;
import com.ezhixuan.codeCraftAi_backend.utils.UserUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 服务层实现。
 *
 * @author Ezhixuan
 */
@Slf4j
@Service
public class SysAppServiceImpl extends ServiceImpl<SysAppMapper, SysApp> implements SysAppService {

  @Resource private CodeCraftFacade codeCraftFacade;
  @Resource private SysChatHistoryService chatHistoryService;
  @Resource private PictureService pictureService;

  @Override
  public Flux<ServerSentEvent<String>> generateCode(String message, Long appId) {
    SysApp sysApp = getById(appId);
    if (isNull(sysApp)) {
      throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
    }
    if (!UserUtil.isMe(sysApp.getUserId())) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }
    Long userId = UserUtil.getLoginUserId();
    // 存储用户对话信息
    chatHistoryService.save(
        SysChatHistorySubmitDto.builder()
            .message(message)
            .userId(userId)
            .messageTypeEnum(MessageTypeEnum.USER)
            .appId(appId)
            .build());
    if (sysApp.isFirstChat()) {
      message = message + PromptConstant.GENERATE_APP_NAME;
      sysApp.setFirstChat(false);
      updateById(sysApp);
    }
    StringBuilder contentBuilder = new StringBuilder();
    CodeGenTypeEnum codeGenType = CodeGenTypeEnum.getByValue(sysApp.getCodeGenType());
    return codeCraftFacade
        .chatAndSaveStream(message, codeGenType, appId)
        .doOnNext(contentBuilder::append)
        .doOnComplete(
            () -> {
              chatHistoryService.save(
                  SysChatHistorySubmitDto.builder()
                      .message(contentBuilder.toString())
                      .userId(userId)
                      .messageTypeEnum(MessageTypeEnum.AI)
                      .appId(appId)
                      .build());
              BuildExecutor.build(appId, codeGenType, true);
            })
        .doOnError(
            error -> {
              log.error("解析失败", error);
              chatHistoryService.save(
                  SysChatHistorySubmitDto.builder()
                      .message("ai 消息回复失败" + error.getMessage())
                      .userId(userId)
                      .messageTypeEnum(MessageTypeEnum.AI)
                      .appId(appId)
                      .build());
            })
        .map(
            chunk -> {
              Map<String, String> d = Map.of("d", chunk);
              String jsonStr = JSONUtil.toJsonStr(d);
              return ServerSentEvent.<String>builder().data(jsonStr).build();
            })
        .concatWith(Mono.just(ServerSentEvent.<String>builder().event("done").data("").build()));
  }

  @Override
  public Long doGenerate(AppGenerateReqVo reqVo) {
    SysApp entity = reqVo.toEntity();
    CodeGenTypeEnum codeGenTypeEnum =
        codeCraftFacade.chatRouter(reqVo.getInitPrompt(), VUE_PROJECT);
    entity.setCodeGenType(codeGenTypeEnum.getValue());
    save(entity);
    return entity.getId();
  }

  @Override
  public Page<SysApp> getList(AppQueryReqVo queryReqVo, boolean limit) {
    return page(queryReqVo.toPage(), getQueryWrapper(queryReqVo, limit));
  }

  @Override
  public Page<AppInfoCommonResVo> convert2Common(
      Page<SysApp> sysAppPage, Map<Long, UserInfoCommonResVo> userInfoMap) {
    if (isNull(sysAppPage) || isEmpty(sysAppPage.getRecords())) {
      return new Page<>();
    }
    return PageRequest.convert(
        sysAppPage,
        sysApp -> AppInfoCommonResVo.build(sysApp, userInfoMap.get(sysApp.getUserId())));
  }

  @Override
  public Page<AppInfoAdminResVo> convert2Admin(
      Page<SysApp> sysAppPage, Map<Long, UserInfoAdminResVo> userInfoMap) {
    if (isNull(sysAppPage) || isEmpty(sysAppPage.getRecords())) {
      return new Page<>();
    }
    return PageRequest.convert(
        sysAppPage, sysApp -> AppInfoAdminResVo.build(sysApp, userInfoMap.get(sysApp.getUserId())));
  }

  /**
   * 重写的删除方法,需要删除对话记录
   *
   * <p>根据数据主键删除数据。
   *
   * @param id 数据主键
   * @return {@code true} 删除成功，{@code false} 删除失败。
   */
  @Override
  public boolean removeById(Serializable id) {
    if (isNull(id)) {
      return false;
    }
    try {
      chatHistoryService.removeByAppId(Long.parseLong(id.toString()));
    } catch (Exception error) {
      log.error("历史记录删除失败, appId = {}", id);
    }
    return super.removeById(id);
  }

  private QueryWrapper getQueryWrapper(AppQueryReqVo queryReqVo, boolean limit) {
    QueryWrapper wrapper = QueryWrapper.create();

    if (limit) {
      wrapper.eq(SysApp::getUserId, UserUtil.getLoginUserId());
    } else {
      wrapper.eq(SysApp::getUserId, queryReqVo.getUserId());
    }

    wrapper.eq(SysApp::getId, queryReqVo.getId());
    wrapper.like(SysApp::getName, queryReqVo.getName());
    wrapper.eq(SysApp::getCodeGenType, queryReqVo.getCodeGenType());
    wrapper.eq(SysApp::getDeployKey, queryReqVo.getDeployKey());
    wrapper.ge(SysApp::getPriority, queryReqVo.getPriority());
    wrapper.ge(SysApp::getUpdateTime, queryReqVo.getStartTime());
    wrapper.le(SysApp::getUpdateTime, queryReqVo.getEndTime());
    wrapper.lt(SysApp::getId, queryReqVo.getMaxId());
    wrapper.orderBy(SysApp::getPriority, Objects.equals(queryReqVo.getOrderBy(), PageRequest.ASC));
    wrapper.orderBy(
        SysApp::getUpdateTime, Objects.equals(queryReqVo.getOrderBy(), PageRequest.ASC));
    return wrapper;
  }

  @Override
  public SysApp getByDeployKey(String deployKey) {
    QueryWrapper wrapper = QueryWrapper.create().eq(SysApp::getDeployKey, deployKey);
    return getOne(wrapper);
  }

  @Override
  public String copyToPreview(Long appId, boolean reBuild) {
    SysApp app = getById(appId);
    if (isNull(app)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用不存在");
    }
    CodeGenTypeEnum codeGenType = CodeGenTypeEnum.getByValue(app.getCodeGenType());

    String targetPath = PathUtil.buildPath(PathUtil.PREVIEW_DIR, codeGenType, appId);
    if (!reBuild || !UserUtil.isAdmin() || !UserUtil.isMe(app.getUserId())) {
      // 不是本人只查看是否存在
      return targetPath.substring(targetPath.lastIndexOf("/") + 1);
    }

    String sourcePath = PathUtil.buildPath(PathUtil.TEMP_DIR, codeGenType, appId);
    String deployPath = PathUtil.buildPath(PathUtil.DEPLOY_DIR, codeGenType, appId);

    if (reBuild || !FileUtil.exist(sourcePath)) {
      copyAndBuildFromOriginal(appId, sourcePath, codeGenType);
    }
    return copyFromSourcePath(sourcePath, targetPath, appId, codeGenType);
  }

  /**
   * 从原始目录复制并构建项目 根据应用ID和代码生成类型从原始目录复制文件，并对Vue项目进行构建
   *
   * @param appId 应用ID
   * @param targetPath 源路径
   * @param codeGenType 代码生成类型枚举
   * @return boolean 复制和构建是否成功
   * @throws BusinessException 当文件不存在或复制失败时抛出业务异常
   */
  private boolean copyAndBuildFromOriginal(
      Long appId, String targetPath, CodeGenTypeEnum codeGenType) {
    try {
      File sourceDir = FileUtil.file(targetPath);
      // copy original 的文件
      String originalPath = PathUtil.buildPath(PathUtil.ORIGINAL_DIR, codeGenType, appId);
      File originalDir = FileUtil.file(originalPath);
      if (!originalDir.exists()) {
        log.error("应用id:{}不存在原始文件,请检查 originalPath:{}", appId, originalPath);
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件不存在");
      }
      FileUtil.copyContent(originalDir, sourceDir, true);
      if (Objects.equals(codeGenType, VUE_PROJECT)) {
        // copy 过来需要等待重新部署
        return BuildExecutor.build(targetPath, codeGenType, false);
      }
    } catch (IORuntimeException e) {
      log.error("应用id:{}复制文件失败,请检查 targetPath:{}", appId, targetPath);
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件复制失败");
    }
    return true;
  }

  /**
   * 从源路径复制文件到目标路径 根据代码生成类型处理不同的源目录，并将文件复制到目标路径
   *
   * @param sourcePath 源路径
   * @param targetPath 目标路径
   * @param appId 用于生成截图时的数据查询
   * @param codeGenType 代码生成类型枚举
   * @return String 目标路径的最后一级目录名
   */
  private String copyFromSourcePath(
      String sourcePath, String targetPath, long appId, CodeGenTypeEnum codeGenType) {
    File sourceDir = FileUtil.file(sourcePath);
    if (Objects.equals(codeGenType, VUE_PROJECT)) {
      sourceDir = FileUtil.file(sourceDir, "/dist");
    }

    File targetDir = FileUtil.file(targetPath);
    FileUtil.clean(targetDir);
    FileUtil.copyContent(sourceDir, targetDir, true);
    String previewKey = targetPath.substring(targetPath.lastIndexOf("/") + 1);
    updateAppCover(appId, previewKey);
    return previewKey;
  }

  @Override
  public void redirect(String previewKey, HttpServletResponse response) {
    String redirectUrl = getUrl(previewKey, false);
    try {
      response.sendRedirect(redirectUrl);
    } catch (IOException e) {
      log.error("重定向失败, redirectUrl:{}", redirectUrl);
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "跳转失败");
    }
  }

  @Override
  public AppStatusResVo getStatus(Long appId) {
    AppStatusResVo appStatusResVo = new AppStatusResVo();

    SysApp sysApp = getById(appId);
    if (isNull(sysApp)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用不存在");
    }
    CodeGenTypeEnum codeGenType = CodeGenTypeEnum.getByValue(sysApp.getCodeGenType());

    appStatusResVo.setDeployStatus(
        FileUtil.exist(PathUtil.buildPath(PathUtil.DEPLOY_DIR, codeGenType, appId))
            ? LoadingStatusEnum.LOADED
            : LoadingStatusEnum.ERROR);

    appStatusResVo.setPreviewStatus(
        FileUtil.exist(PathUtil.buildPath(PathUtil.PREVIEW_DIR, codeGenType, appId))
            ? LoadingStatusEnum.LOADED
            : LoadingStatusEnum.ERROR);

    appStatusResVo.setOriginalDirStatus(
        FileUtil.exist(PathUtil.buildPath(PathUtil.ORIGINAL_DIR, codeGenType, appId))
            ? LoadingStatusEnum.LOADED
            : LoadingStatusEnum.ERROR);

    return appStatusResVo;
  }

  @Override
  public String doDeploy(Long appId) {
    SysApp sysApp = getById(appId);
    if (isNull(sysApp)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用不存在");
    }
    CodeGenTypeEnum codeGenType = CodeGenTypeEnum.getByValue(sysApp.getCodeGenType());
    String deployPath = PathUtil.buildPath(PathUtil.DEPLOY_DIR, codeGenType, appId);
    FileUtil.clean(deployPath);
    copyAndBuildFromOriginal(appId, deployPath, codeGenType);
    return deployPath.substring(deployPath.lastIndexOf("/") + 1);
  }

  @Override
  public void doZip(Long appId, HttpServletResponse response) {
    SysApp sysApp = getById(appId);
    if (isNull(sysApp)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用不存在");
    }
    CodeGenTypeEnum codeGenType = CodeGenTypeEnum.getByValue(sysApp.getCodeGenType());
    String zipPath = PathUtil.buildPath(PathUtil.ORIGINAL_DIR, codeGenType, appId);
    try (OutputStream outputStream = response.getOutputStream()) {
      // 设置返回头
      response.setContentType("application/zip");
      // 对文件名进行URL编码，解决包含中文字符时HTTP头无效的问题
      String encodedFileName = URLEncoder.encode(sysApp.getName() + ".zip", StandardCharsets.UTF_8);
      response.setHeader(
          "Content-Disposition", String.format("attachment; filename*=UTF-8''%s", encodedFileName));
      ZipUtil.zip(outputStream, StandardCharsets.UTF_8, false, null, FileUtil.file(zipPath));
    } catch (IOException e) {
      log.error("应用id:{}下载失败", appId, e);
    }
  }

  /**
   * 异步更新应用封面
   *
   * @param appId 应用ID
   * @param previewKey 预览键值
   */
  private void updateAppCover(long appId, String previewKey) {
    Thread.startVirtualThread(
        () -> {
          try {
            SysApp sysApp = getById(appId);
            String cover = sysApp.getCover();
            if (isNull(sysApp)) {
              throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用不存在");
            }
            String webUrl = getUrl(previewKey, true);
            String newCover = screenshot(appId, webUrl);
            if (hasText(newCover)) {
              if (!hasText(cover)) {
                // 应用本身就有封面
                pictureService.delete(cover);
              }
              sysApp.setCover(newCover);
              updateById(sysApp);
            }
          } catch (Exception e) {
            log.error("更新应用封面失败, appId:{}", appId);
          }
        });
  }

  /**
   * 对指定URL进行截图并上传
   *
   * @param appId 应用ID
   * @param webUrl 需要截图的网页URL
   * @return 上传后的图片访问路径，失败时返回空字符串
   */
  private String screenshot(long appId, String webUrl) {
    File screenshot = null;
    try {
      screenshot = ScreenshotUtil.screenshot(webUrl, true);
      String uploadPath =
          LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
              + File.separator
              + screenshot.getName();
      return pictureService.upload(screenshot, uploadPath);
    } catch (Exception exception) {
      log.error("应用id:{}截图失败,请检查 webUrl:{}", appId, webUrl);
      return "";
    } finally {
        if (nonNull(screenshot)) {
          screenshot.delete();
        }
    }
  }

  /**
   * 获取预览URL
   *
   * @param previewKey 预览键值
   * @param hasLocal 是否包含本地地址
   * @return 根据参数生成的URL
   */
  private String getUrl(String previewKey, boolean hasLocal) {
    return hasLocal
        ? "http://localhost:8911/api/static/" + previewKey + "/index.html"
        : "/api/static/" + previewKey + "/index.html";
  }
}
