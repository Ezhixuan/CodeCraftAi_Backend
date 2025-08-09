package com.ezhixuan.codeCraftAi_backend.core.parse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CodeParser<T> {

    /**
     * 消息解析
     * @author Ezhixuan
     * @param content 消息内容
     * @return T
     */
    public abstract T parse(String content);

    /**
     * 提取内容中的描述部分（非代码块部分）
     * @param content 原始内容
     * @return 描述内容
     */
    protected String extractDescription(String content) {
        if (content == null) {
            return null;
        }

        // 移除所有代码块，保留描述文本
        String description = content
                .replaceAll("```html[\\s\\S]*?```", "")
                .replaceAll("```css[\\s\\S]*?```", "")
                .replaceAll("```javascript[\\s\\S]*?```", "")
                .replaceAll("```js[\\s\\S]*?```", "")
                .trim();

        return description.isEmpty() ? null : description;
    }
}
