package com.ezhixuan.codeCraftAi_backend.service.impl;

import com.ezhixuan.codeCraftAi_backend.common.PageRequest;
import com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo.ChatInfoResVo;
import com.ezhixuan.codeCraftAi_backend.controller.chatHistory.vo.ChatQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.domain.dto.sys.chatHistory.SysChatHistorySubmitDto;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysChatHistory;
import com.ezhixuan.codeCraftAi_backend.mapper.SysChatHistoryMapper;
import com.ezhixuan.codeCraftAi_backend.service.SysChatHistoryService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * 对话历史 服务层实现。
 *
 * @author Ezhixuan
 */
@Service
public class SysChatHistoryServiceImpl extends ServiceImpl<SysChatHistoryMapper, SysChatHistory>  implements SysChatHistoryService{

    @Override
    public boolean save(SysChatHistorySubmitDto chatHistorySubmitDto) {
        SysChatHistory entity = chatHistorySubmitDto.toEntity();
        return save(entity);
    }

    @Override
    public boolean removeByAppId(Long appId) {
        if (isNull(appId)) {
            return false;
        }
        QueryWrapper queryWrapper = QueryWrapper.create().eq(SysChatHistory::getAppId, appId);
        return remove(queryWrapper);
    }

    @Override
    public Page<ChatInfoResVo> list(ChatQueryReqVo reqVo) {
        QueryWrapper queryWrapper = getQueryWrapper(reqVo);
        return PageRequest.convert(page(reqVo.toPage(), queryWrapper), ChatInfoResVo::new);
    }

    private QueryWrapper getQueryWrapper(ChatQueryReqVo reqVo) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (nonNull(reqVo.getAppId()) && reqVo.getAppId() > 0) {
            // 管理员查询全部时默认传-1
            queryWrapper.eq(SysChatHistory::getAppId, reqVo.getAppId());
        }
        queryWrapper.eq(SysChatHistory::getId, reqVo.getId());
        queryWrapper.eq(SysChatHistory::getMessageType, reqVo.getMessageType());
        queryWrapper.eq(SysChatHistory::getUserId, reqVo.getUserId());
        queryWrapper.lt(SysChatHistory::getCreateTime, reqVo.getEndTime());
        queryWrapper.gt(SysChatHistory::getCreateTime, reqVo.getStartTime());
        queryWrapper.orderBy(SysChatHistory::getCreateTime, Objects.equals(reqVo.getOrderBy(), PageRequest.ASC));
        return queryWrapper;
    }
}
