package com.ezhixuan.codeCraftAi_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysChatHistory;
import com.ezhixuan.codeCraftAi_backend.mapper.SysChatHistoryMapper;
import com.ezhixuan.codeCraftAi_backend.service.SysChatHistoryService;
import org.springframework.stereotype.Service;

/**
 * 对话历史 服务层实现。
 *
 * @author Ezhixuan
 */
@Service
public class SysChatHistoryServiceImpl extends ServiceImpl<SysChatHistoryMapper, SysChatHistory>  implements SysChatHistoryService{

}
