package com.ezhixuan.codeCraftAi_backend.ai.model.enums;

import cn.hutool.core.util.ObjUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public enum CodeGenTypeEnum {

    HTML("原生 HTML 模式", "html"),
    HTML_MULTI_FILE("原生多文件模式", "multi_file"),
    VUE_PROJECT("Vue 模式", "vue_project");

    @Schema(description = "枚举文本")
    private final String text;

    @Schema(description = "代码生成模式")
    private final String value;

    CodeGenTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static CodeGenTypeEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (CodeGenTypeEnum anEnum : CodeGenTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
