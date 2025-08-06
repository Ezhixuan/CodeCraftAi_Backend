package com.ezhixuan.ai.codeCraftAi_backend.common;

import java.io.Serial;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PageResponse<T> extends BaseResponse<List<T>> {

    @Serial
    private static final long serialVersionUID = -8234567890123456789L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private int current;

    /**
     * 每页大小
     */
    private int size;

    /**
     * 总页数
     */
    private long pages;

    public PageResponse(int code, List<T> data, String message, long total, int current, int size) {
        super(code, data, message);
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = size > 0 ? (total + size - 1) / size : 0;
    }

    public PageResponse(List<T> data, long total, int current, int size) {
        this(200, data, "ok", total, current, size);
    }
}
