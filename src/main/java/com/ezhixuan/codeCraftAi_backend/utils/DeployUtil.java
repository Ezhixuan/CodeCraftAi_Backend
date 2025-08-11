package com.ezhixuan.codeCraftAi_backend.utils;

import static org.springframework.util.StringUtils.hasText;

import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;

import cn.hutool.core.util.IdUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DeployUtil {

    /**
     * 密钥初始化
     * @author Ezhixuan
     * @return String 密钥
     */
    public String initDeployKey() {
        return IdUtil.getSnowflakeNextIdStr() + "-" + 0;
    }

    /**
     * 校验密钥是否合规 反转
     * @author Ezhixuan
     * @param deployKey 密钥
     * @return boolean 是否合规
     */
    public boolean checkDeployKey(String deployKey) {
        if (!hasText(deployKey)) {
            return true;
        }
        String[] split = deployKey.split("-");
        if (split.length != 2) {
            return true;
        }
        try {
            Long.parseLong(split[0]);
        } catch (Exception e) {
            return true;
        }
        try {
            Integer.parseInt(split[1]);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * 获取版本号
     * @author Ezhixuan
     * @param deployKey 密钥
     * @return int
     */
    public int getVersion(String deployKey) {
        String[] split = deployKey.split("-");
        if (split.length != 2) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署密钥格式错误");
        }
        return Integer.parseInt(split[1]);
    }

    /**
     * 更新版本
     * @author Ezhixuan
     * @param deployKey 密钥
     * @return String 更新后的密钥
     */
    public String newVersion(String deployKey) {
        int version = getVersion(deployKey);
        return deployKey.split("-")[0] + "-" + (version + 1);
    }
}
