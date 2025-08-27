package com.ezhixuan.codeCraftAi_backend.core.saver;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class FileSaverDto {

    @NotNull
    private Object aiChatDto;

    @NotNull
    private CodeGenTypeEnum codeGenTypeEnum;

    @NotNull
    @Min(value = 1)
    private Long appId;
}
