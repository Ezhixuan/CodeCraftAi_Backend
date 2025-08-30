package com.ezhixuan.codeCraftAi_backend.service.impl;

import cn.hutool.core.io.FileUtil;
import com.ezhixuan.codeCraftAi_backend.manager.oss.OssManager;
import com.ezhixuan.codeCraftAi_backend.service.PictureService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class PictureServiceImpl implements PictureService {

    @Resource private OssManager ossManager;

    @Override
    public String upload(File picture, String targetPath) {
        return ossManager.getInstance().doUpload(FileUtil.getInputStream(picture), targetPath);
    }

    @Override
    public boolean delete(String pictureUrl) {
        return ossManager.getInstance().doDelete(pictureUrl);
    }
}
