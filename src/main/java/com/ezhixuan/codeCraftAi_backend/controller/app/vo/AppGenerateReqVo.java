package com.ezhixuan.codeCraftAi_backend.controller.app.vo;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.domain.constant.AppConstant;
import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.codeCraftAi_backend.utils.DeployUtil;
import com.ezhixuan.codeCraftAi_backend.utils.UserUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

import static java.util.Objects.isNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppGenerateReqVo extends AppConverter implements Serializable {

    @Serial
    private static final long serialVersionUID = 4237947024329138431L;

    @NotBlank(message = "提问不能为空")
    @Schema(description = "初始化提示")
    private String initPrompt;

    @NotBlank(message = "代码生成类型不能为空")
    @Schema(description = "代码生成类型")
    private String codeGenType;

    @Override
    public SysApp toEntity() {
        CodeGenTypeEnum enumByValue = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (isNull(enumByValue)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码生成类型错误");
        }
        SysApp entity = new SysApp();
        entity.setName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        entity.setInitPrompt(initPrompt);
        entity.setPriority(AppConstant.BASE_PRIORITY);
        entity.setUserId(UserUtil.getLoginUserId());
        entity.setCodeGenType(enumByValue.getValue());
        entity.setDeployKey(DeployUtil.initDeployKey());
        return entity;
    }
}
