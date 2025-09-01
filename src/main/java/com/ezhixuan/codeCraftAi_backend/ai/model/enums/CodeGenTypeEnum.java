package com.ezhixuan.codeCraftAi_backend.ai.model.enums;

import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum CodeGenTypeEnum {

    HTML("原生 HTML 模式", "html"),
    HTML_MULTI_FILE("原生多文件模式", "multi_file"),
    VUE_PROJECT("Vue 模式", "vue_project");

    @Schema(description = "枚举文本")
    private final String desc;

    @Schema(description = "代码生成模式")
    private final String value;

    CodeGenTypeEnum(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static CodeGenTypeEnum getByValue(String value) {
        for (CodeGenTypeEnum anEnum : CodeGenTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        log.error("存在代码生成类型错误,请检查 codeGenType:{}", value);
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码生成类型错误");
    }
}
