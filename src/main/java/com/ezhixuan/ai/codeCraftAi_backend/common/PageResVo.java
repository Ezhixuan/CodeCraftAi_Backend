package com.ezhixuan.ai.codeCraftAi_backend.common;

import java.util.ArrayList;
import java.util.List;

import com.mybatisflex.core.paginate.Page;

import lombok.Data;

@Data
public class PageResVo<T> {

    /**
     * 总记录数
     */
    private long totalRow;

    /**
     * 数据
     */
    private List<T> list;

    /**
     * 总页数
     */
    private long totalPage;

    public PageResVo(List<T> list) {
        this.list = list;
        this.totalRow = list.size();
        this.totalPage = 1;
    }

    public PageResVo() {
        this.totalRow = 0;
        this.totalPage = 1;
        this.list = new ArrayList<>();
    }

    public PageResVo(Page<T> page) {
        this.list = page.getRecords();
        this.totalPage = page.getTotalPage();
        this.totalRow = page.getTotalRow();
    }
}
