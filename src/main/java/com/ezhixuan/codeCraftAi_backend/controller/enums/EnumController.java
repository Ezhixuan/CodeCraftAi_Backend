package com.ezhixuan.codeCraftAi_backend.controller.enums;

import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.enums.vo.KeyValueResVo;
import com.ezhixuan.codeCraftAi_backend.domain.enums.UserRoleEnum;
import com.ezhixuan.codeCraftAi_backend.domain.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/enums")
@RestController
@Tag(name = "enumController", description = "枚举控制器")
public class EnumController {

  @Operation(summary = "获取代码生成模式列表")
  @GetMapping("/codeGenType")
  public PageResponse<KeyValueResVo> getCodeGenTypeList() {
    return R.list(
        KeyValueResVo.list(
            CodeGenTypeEnum.class, CodeGenTypeEnum::getValue, CodeGenTypeEnum::getDesc));
  }

  @Operation(summary = "获取用户状态列表")
  @GetMapping("/user/status")
  public PageResponse<KeyValueResVo> getUserStatusList() {
    return R.list(
        KeyValueResVo.list(
            UserStatusEnum.class, UserStatusEnum::getValue, UserStatusEnum::getDesc));
  }

  @Operation(summary = "获取用户角色列表")
  @GetMapping("/user/role")
  public PageResponse<KeyValueResVo> getUserRoleList() {
    return R.list(
        KeyValueResVo.list(UserRoleEnum.class, UserRoleEnum::getValue, UserRoleEnum::getDesc));
  }
}
