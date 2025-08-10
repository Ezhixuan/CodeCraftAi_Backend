package com.ezhixuan.codeCraftAi_backend.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ezhixuan.codeCraftAi_backend.common.PageRequest;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppGenerateReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.ezhixuan.codeCraftAi_backend.mapper.SysAppMapper;
import com.ezhixuan.codeCraftAi_backend.service.SysAppService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

/**
 *  服务层实现。
 *
 * @author Ezhixuan
 */
@Service
public class SysAppServiceImpl extends ServiceImpl<SysAppMapper, SysApp>  implements SysAppService{

    @Override
    public Long doGenerate(AppGenerateReqVo reqVo) {
        SysApp entity = reqVo.toEntity();
        save(entity);
        return entity.getId();
    }

    @Override
    public Page<AppInfoCommonResVo> getList(AppQueryReqVo queryReqVo) {
        QueryWrapper wrapper = getQueryWrapper(queryReqVo);
    }

    private QueryWrapper getQueryWrapper(AppQueryReqVo queryReqVo) {
        String ASC = PageRequest.ASC;
        Integer PAGE_SIZE_NONE = PageRequest.PAGE_SIZE_NONE;
        Long id = queryReqVo.getId();
        String name = queryReqVo.getName();
        String codeGenType = queryReqVo.getCodeGenType();
        String deployKey = queryReqVo.getDeployKey();
        LocalDateTime deployTime = queryReqVo.getDeployTime();
        Integer priority = queryReqVo.getPriority();
        Long userId = queryReqVo.getUserId();
        LocalDateTime createTime = queryReqVo.getCreateTime();
        LocalDateTime updateTime = queryReqVo.getUpdateTime();
        Integer pageNo = queryReqVo.getPageNo();
        Integer pageSize = queryReqVo.getPageSize();
        String orderBy = queryReqVo.getOrderBy();

        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq(SysApp::getId, queryReqVo.getId());
        wrapper.like(SysApp::getName, queryReqVo.getName());
        wrapper.eq(SysApp::getCodeGenType, queryReqVo.getCodeGenType());
        if ()
    }
}
