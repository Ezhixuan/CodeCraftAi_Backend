package com.ezhixuan.codeCraftAi_backend.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.common.PageRequest;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppGenerateReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.core.CodeCraftFacade;
import com.ezhixuan.codeCraftAi_backend.core.builder.BuildExecutor;
import com.ezhixuan.codeCraftAi_backend.domain.dto.sys.chatHistory.SysChatHistorySubmitDto;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.ezhixuan.codeCraftAi_backend.domain.enums.MessageTypeEnum;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.codeCraftAi_backend.mapper.SysAppMapper;
import com.ezhixuan.codeCraftAi_backend.service.SysAppService;
import com.ezhixuan.codeCraftAi_backend.service.SysChatHistoryService;
import com.ezhixuan.codeCraftAi_backend.utils.PathUtil;
import com.ezhixuan.codeCraftAi_backend.utils.UserUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import static com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum.VUE_PROJECT;
import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

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
    StringBuilder contentBuilder = new StringBuilder();
    CodeGenTypeEnum codeGenType = CodeGenTypeEnum.getEnumByValue(sysApp.getCodeGenType());
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
        SysApp::getUpdateTime, Objects.equals(queryReqVo.getOrderBy(), PageRequest.DESC));
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
    CodeGenTypeEnum codeGenType = CodeGenTypeEnum.getEnumByValue(app.getCodeGenType());
    if (isNull(codeGenType)) {
      log.error("应用id:{}存在代码生成类型错误,请检查 codeGenType:{}", appId, app.getCodeGenType());
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码生成类型错误");
    }
    String targetPath = PathUtil.buildPath(PathUtil.PREVIEW_DIR, codeGenType, appId);
    if (!UserUtil.isAdmin() || !UserUtil.isMe(app.getUserId())) {
      // 不是本人只查看是否存在
      return targetPath.substring(targetPath.lastIndexOf("/") + 1);
    }
    String sourcePath = PathUtil.buildPath(PathUtil.TEMP_DIR, codeGenType, appId);
    if (reBuild || !FileUtil.exist(sourcePath)) {
      File sourceDir = FileUtil.file(sourcePath);
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
        BuildExecutor.build(sourcePath, codeGenType, false);
      }
    }

    File sourceDir = FileUtil.file(sourcePath);
    if (Objects.equals(codeGenType, VUE_PROJECT)) {
      sourceDir = FileUtil.file(sourceDir, "/dist");
    }

    File targetDir = FileUtil.file(targetPath);
    FileUtil.clean(targetDir);
    FileUtil.copyContent(sourceDir, targetDir, true);

    return targetPath.substring(targetPath.lastIndexOf("/") + 1);
  }

  @Override
  public void redirect(String previewKey, HttpServletResponse response) {
    String redirectUrl = "/api/static/" + previewKey + "/index.html";
    try {
      response.sendRedirect(redirectUrl);
    } catch (IOException e) {
      log.error("重定向失败, redirectUrl:{}", redirectUrl);
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "跳转失败");
    }
  }
}
