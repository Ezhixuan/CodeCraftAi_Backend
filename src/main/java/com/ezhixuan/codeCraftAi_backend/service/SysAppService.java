package com.ezhixuan.codeCraftAi_backend.service;

import java.util.Map;

import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppGenerateReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoCommonResVo;
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
     * @param limit 查询限制
     * @return Page<AppInfoCommonResVo>
     */
    Page<SysApp> getList(AppQueryReqVo queryReqVo, boolean limit);

    /**
     * 提供 common 返回
     * @author Ezhixuan
     * @param sysAppPage getList 返回的 Page
     * @param userInfoMap 用户信息
     * @return Page<AppInfoCommonResVo> common 返回
     */
    Page<AppInfoCommonResVo> convert2Common(Page<SysApp> sysAppPage, Map<Long, UserInfoCommonResVo> userInfoMap);

    /**
     * 提供 admin 返回
     * @author Ezhixuan
     * @param sysAppPage getList 返回的 Page
     * @param userInfoMap 用户信息
     * @return Page<AppInfoAdminResVo> admin 返回
     */
    Page<AppInfoAdminResVo> convert2Admin(Page<SysApp> sysAppPage, Map<Long, UserInfoAdminResVo> userInfoMap);
}
