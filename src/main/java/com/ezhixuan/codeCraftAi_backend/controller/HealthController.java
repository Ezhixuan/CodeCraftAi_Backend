package com.ezhixuan.codeCraftAi_backend.controller;

import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "HealthController", description = "健康检查控制器")
@RestController
public class HealthController {

  @Operation(summary = "健康检查")
  @GetMapping("/health")
  public BaseResponse<String> isOk() {
    return R.SUCCESS;
  }

  @Operation(summary = "列表")
  @GetMapping("/list")
  public PageResponse<String> list() {
    List<String> list = new ArrayList<>();
    list.add("1");
    return R.list(list);
  }
}
