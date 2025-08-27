package com.ezhixuan.codeCraftAi_backend.core.parser;

import lombok.extern.slf4j.Slf4j;

/**
 * 代码解析器抽象类 定义代码解析的通用接口和基础功能，用于解析AI生成的代码内容 子类需要实现具体的解析逻辑
 *
 * @author ezhixuan
 * @version 0.0.1beta
 * @param <T> 解析结果的类型
 */
@Slf4j
public abstract class CodeParser<T> {

  /**
   * 抽象解析方法 由子类实现具体的代码解析逻辑
   *
   * @param content 待解析的内容
   * @return 解析结果
   */
  public abstract T parse(String content);

  /**
   * 提取描述文本 从内容中移除代码块，提取纯文本描述部分
   *
   * @param content 原始内容
   * @return 提取的描述文本，如果无描述则返回null
   */
  protected String extractDescription(String content) {
    if (content == null) {
      return null;
    }

    // 移除所有代码块，保留描述文本
    String description =
        content
            .replaceAll("```html[\\s\\S]*?```", "")
            .replaceAll("```css[\\s\\S]*?```", "")
            .replaceAll("```javascript[\\s\\S]*?```", "")
            .replaceAll("```js[\\s\\S]*?```", "")
            .trim();

    return description.isEmpty() ? null : description;
  }
}
