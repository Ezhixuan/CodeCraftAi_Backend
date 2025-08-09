package com.ezhixuan.codeCraftAi_backend.controller.user.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import org.hibernate.validator.constraints.Length;

import com.ezhixuan.codeCraftAi_backend.domain.entity.SysUser;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.codeCraftAi_backend.service.SysUserService;
import com.ezhixuan.codeCraftAi_backend.utils.UserUtil;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateReqVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -3825862780236674142L;

    @Schema(description = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long id;

    @Schema(description = "用户密码")
    @Length(min = 6, message = "密码长度过短")
    private String password;

    @Schema(description = "用户昵称")
    @Length(min = 1, max = 20, message = "昵称长度在1-20个字符之间")
    private String name;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户简介")
    private String profile;

    @Schema(description = "用户邮箱")
    private String email;

    public SysUser toUser(SysUserService service) {
        if (!Objects.equals(id, UserUtil.getLoginUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        SysUser user = new SysUser();
        user.setId(id);
        if (Objects.nonNull(password)) {
            user.setPassword(service.getEncryptedPassword(password));
        }
        user.setName(name);
        user.setAvatar(avatar);
        user.setProfile(profile);
        user.setEmail(email);
        return user;
    }

}
