package com.ezhixuan.codeCraftAi_backend.controller.enums.vo;

import cn.hutool.core.util.EnumUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

@Data
public class KeyValueResVo {

  @Schema(description = "枚举 key")
  private String key;

  @Schema(description = "枚举 value")
  private String value;

  public KeyValueResVo(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public static <T extends Enum<T>> List<KeyValueResVo> list(
      Class<T> enumClass, Function<T, Object> keyFunction, Function<T, Object> valueFunction) {
    LinkedHashMap<String, T> enumMap = EnumUtil.getEnumMap(enumClass);
    return enumMap.values().stream()
        .map(
            enumObj ->
                new KeyValueResVo(
                    keyFunction.apply(enumObj).toString(), valueFunction.apply(enumObj).toString()))
        .toList();
  }
}
