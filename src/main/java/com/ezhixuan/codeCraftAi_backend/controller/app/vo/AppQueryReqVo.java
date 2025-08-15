package com.ezhixuan.codeCraftAi_backend.controller.app.vo;

import com.ezhixuan.codeCraftAi_backend.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppQueryReqVo extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 4259822200330189808L;

    @Schema(description = "应用ID")
    private Long id;

    @Schema(description = "应用名称")
    private String name;

    @Schema(description = "代码生成类型")
    private String codeGenType;

    @Schema(description = "部署密钥")
    private String deployKey;

    @Schema(description = "部署时间")
    private LocalDateTime deployTime;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "用户 id")
    private Long userId;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "最大 id")
    private Long maxId;
}
