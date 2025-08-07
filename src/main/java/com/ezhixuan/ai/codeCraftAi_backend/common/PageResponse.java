package com.ezhixuan.ai.codeCraftAi_backend.common;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageResponse<T> extends BaseResponse<PageResVo<T>> implements Serializable {

    @Serial
    private static final long serialVersionUID = -8234567890123456789L;

    public PageResponse(PageResVo<T> data) {
        super(data);
    }

    public PageResponse() {
        super();
    }
}
