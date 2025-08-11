package com.ezhixuan.codeCraftAi_backend.service.impl;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.common.PageRequest;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppGenerateReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.core.CodeCraftFacade;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.codeCraftAi_backend.mapper.SysAppMapper;
import com.ezhixuan.codeCraftAi_backend.service.SysAppService;
import com.ezhixuan.codeCraftAi_backend.utils.UserUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *  服务层实现。
 *
 * @author Ezhixuan
 */
@Service
public class SysAppServiceImpl extends ServiceImpl<SysAppMapper, SysApp>  implements SysAppService{

    @Resource
    private CodeCraftFacade codeCraftFacade;

    @Override
    public Flux<ServerSentEvent<String>> generateCode(String message, Long appId) {
        SysApp sysApp = getById(appId);
        if (isNull(sysApp)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (!UserUtil.isMe(sysApp.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return codeCraftFacade.chatAndSaveStream(message, CodeGenTypeEnum.getEnumByValue(sysApp.getCodeGenType()), appId)
                .map(chunk -> {
                    Map<String, String> d = Map.of("d", chunk);
                    String jsonStr = JSONUtil.toJsonStr(d);
                    return ServerSentEvent.<String>builder()
                            .data(jsonStr)
                            .build();
                })
                .concatWith(Mono.just(
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build()
                ));
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
    public Page<AppInfoCommonResVo> convert2Common(Page<SysApp> sysAppPage, Map<Long, UserInfoCommonResVo> userInfoMap) {
        if (isNull(sysAppPage) || isEmpty(sysAppPage.getRecords())) {
            return new Page<>();
        }
        return PageRequest.convert(sysAppPage, sysApp -> {
            AppInfoCommonResVo appInfoCommonResVo = new AppInfoCommonResVo();
            appInfoCommonResVo.build(sysApp, userInfoMap.get(sysApp.getUserId()));
            return appInfoCommonResVo;
        });
    }

    @Override
    public Page<AppInfoAdminResVo> convert2Admin(Page<SysApp> sysAppPage, Map<Long, UserInfoAdminResVo> userInfoMap) {
        if (isNull(sysAppPage) || isEmpty(sysAppPage.getRecords())) {
            return new Page<>();
        }
        return PageRequest.convert(sysAppPage, sysApp -> {
            AppInfoAdminResVo appInfoAdminResVo = new AppInfoAdminResVo();
            appInfoAdminResVo.build(sysApp, userInfoMap.get(sysApp.getUserId()));
            return appInfoAdminResVo;
        });
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
        wrapper.le(SysApp::getPriority, queryReqVo.getPriority());
        wrapper.ge(SysApp::getUpdateTime, queryReqVo.getStartTime());
        wrapper.le(SysApp::getUpdateTime, queryReqVo.getEndTime());
        wrapper.orderBy(SysApp::getPriority, Objects.equals(queryReqVo.getOrderBy(), PageRequest.ASC));
        return wrapper;
    }

    @Override
    public SysApp getByDeployKey(String deployKey) {
        QueryWrapper wrapper = QueryWrapper.create()
                .eq(SysApp::getDeployKey, deployKey);
        return getOne(wrapper);
    }
}
