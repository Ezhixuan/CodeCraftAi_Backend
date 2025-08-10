package com.ezhixuan.codeCraftAi_backend.service;

import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppGenerateReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 *  服务层。
 *
 * @author Ezhixuan
 */
public interface SysAppService extends IService<SysApp> {

    /**
     * 通过用户输入内容生成记录
     * @author Ezhixuan
     * @param reqVo 请求体,携带初始化提示
     * @return Long
     */
    Long doGenerate(AppGenerateReqVo reqVo);

    /**
     * 获取用户应用列表
     * @author Ezhixuan
     * @param queryReqVo 请求体
     * @return Page<AppInfoCommonResVo>
     */
    Page<AppInfoCommonResVo> getList(AppQueryReqVo queryReqVo);
}
