package com.ezhixuan.codeCraftAi_backend.service;

import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppGenerateReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppInfoCommonResVo;
import com.ezhixuan.codeCraftAi_backend.controller.app.vo.AppQueryReqVo;
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
 *  服务层。
 *
 * @author Ezhixuan
 */
public interface SysAppService extends IService<SysApp> {

    /**
     * 代码生成
     * @author Ezhixuan
     * @param message 用户输入
     * @param appId 应用id
     * @return Flux<ServerSentEvent < String>> sse流式返回
     */
    Flux<ServerSentEvent<String>> generateCode(String message, Long appId);

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

    /**
     * 通过部署标识获取应用
     * @author Ezhixuan
     * @param deployKey 部署标识
     * @return SysApp 应用信息
     */
    SysApp getByDeployKey(String deployKey);

    /**
     * 将文件复制到 preview 文件夹
     *
     * @param appId   应用 id
     * @param reBuild 重构 只有应用用户为当前用户才可以进行
     * @return String
     * @author Ezhixuan
     */
    String copyToPreview(Long appId, boolean reBuild);

    /**
     * 重定向到预览页面
     * @author Ezhixuan
     * @param previewKey 预览标识
     * @param response 响应
     */
    void redirect(String previewKey, HttpServletResponse response);
}
