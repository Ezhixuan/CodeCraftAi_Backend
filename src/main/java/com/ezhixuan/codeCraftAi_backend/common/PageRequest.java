package com.ezhixuan.codeCraftAi_backend.common;

import java.util.Objects;

import com.mybatisflex.core.paginate.Page;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageRequest {

    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;

    public static final String ASC = "asc";
    public static final Integer PAGE_SIZE_NONE = -1;

    @Schema(description = "页码，从 1 开始")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    private Integer pageNo = PAGE_NO;

    @Schema(description = "每页条数，最大值为 100")
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 100, message = "每页条数最大值为 100")
    private Integer pageSize = PAGE_SIZE;

    @Schema(description = "排序方式")
    private String orderBy = ASC;

    public <T> Page<T> toPage(){
        if (Objects.equals(pageSize, PAGE_SIZE_NONE)) {
            return new Page<>(pageNo, Integer.MAX_VALUE);
        }
        return new Page<>(pageNo, pageSize);
    }

    public static <T, R> Page<R> convert(Page<T> tPage, Class<R> rClass) {
        return tPage.map(t -> BeanUtil.copyProperties(t, rClass));
    }
}
