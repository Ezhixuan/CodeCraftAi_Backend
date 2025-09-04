package com.ezhixuan.codeCraftAi_backend.controller.user;

import com.ezhixuan.codeCraftAi_backend.annotation.AuthRole;
import com.ezhixuan.codeCraftAi_backend.common.BaseResponse;
import com.ezhixuan.codeCraftAi_backend.common.PageResponse;
import com.ezhixuan.codeCraftAi_backend.common.R;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserAddReqVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserAddResVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserInfoAdminResVo;
import com.ezhixuan.codeCraftAi_backend.controller.user.vo.UserQueryReqVo;
import com.ezhixuan.codeCraftAi_backend.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/user")
@RestController
@Validated
@Tag(name = "UserAdminController", description = "用户控制器")
@RequiredArgsConstructor
public class UserAdminController {

  private final SysUserService userService;

  @Operation(summary = "新增用户(支持批量)")
  @AuthRole
  @PostMapping("/add")
  public PageResponse<UserAddResVo> postUserAddAdmin(@RequestBody List<UserAddReqVo> waitAddList) {
    return R.list(userService.saveBatch(waitAddList));
  }

  @Operation(summary = "新增用户(支持批量)")
  @AuthRole
  @PostMapping("/add/{size}")
  public PageResponse<UserAddResVo> postUserAddBySizeAdmin(@PathVariable Integer size) {
    return R.list(userService.saveBatch(size));
  }

  @Operation(summary = "停用账号")
  @AuthRole
  @PutMapping("/disable/{disableId}")
  public BaseResponse<Void> putUserDisabledAdmin(@PathVariable Long disableId) {
    userService.doDisable(disableId);
    return R.success();
  }

  @Operation(summary = "获取用户信息(完整)")
  @AuthRole
  @GetMapping("/{id}")
  public BaseResponse<UserInfoAdminResVo> getUserInfoAdmin(@PathVariable Long id) {
    return R.success(UserInfoAdminResVo.build(userService.getById(id)));
  }

  @Operation(summary = "获取用户列表")
  @AuthRole
  @GetMapping("/list")
  public PageResponse<UserInfoAdminResVo> getUserListAdmin(@Valid UserQueryReqVo queryReqVo) {
    return R.list(userService.getList(queryReqVo));
  }
}
