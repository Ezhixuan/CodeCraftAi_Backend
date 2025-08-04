package com.ezhixuan.common.common_backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezhixuan.common.common_backend.common.BaseResponse;
import com.ezhixuan.common.common_backend.common.PageResponse;
import com.ezhixuan.common.common_backend.common.R;

@RestController
public class HealthController {

    @GetMapping("/health")
    public BaseResponse<String> isOk() {
        return R.SUCCESS;
    }

    @GetMapping("/list")
    public PageResponse<String> list() {
        List<String> list = new ArrayList<>();
        list.add("1");
        return R.list(list);
    }
}
