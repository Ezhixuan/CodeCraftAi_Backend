package com.ezhixuan.codeCraftAi_backend.controller.app.vo;

import java.io.Serial;
import java.io.Serializable;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.domain.constant.AppConstant;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.ezhixuan.codeCraftAi_backend.utils.DeployUtil;
import com.ezhixuan.codeCraftAi_backend.utils.UserUtil;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppGenerateReqVo extends AppConverter implements Serializable {

    @Serial
    private static final long serialVersionUID = 4237947024329138431L;

    @NotBlank(message = "提问不能为空")
    @Schema(description = "初始化提示")
    private String initPrompt;

    @Override
    public SysApp toEntity() {
        SysApp entity = new SysApp();
        entity.setName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        entity.setInitPrompt(initPrompt);
        entity.setPriority(AppConstant.BASE_PRIORITY);
        entity.setUserId(UserUtil.getLoginUserId());
        entity.setCodeGenType(CodeGenTypeEnum.HTML_MULTI_FILE.getValue());
        entity.setDeployKey(DeployUtil.initDeployKey());
        return entity;
    }
}
