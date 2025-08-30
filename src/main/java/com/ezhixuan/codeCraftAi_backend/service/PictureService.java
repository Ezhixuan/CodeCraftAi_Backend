package com.ezhixuan.codeCraftAi_backend.service;

import java.io.File;

/**
 * 图片上传服务接口.定义统一的图片上传规范
 *
 * @version 0.0.2beta
 * @author ezhixuan
 */
public interface PictureService {

  /**
   * 上传图片文件到指定路径
   *
   * @param picture 待上传的图片文件
   * @param targetPath 目标存储路径
   * @return 上传成功后的文件访问URL
   */
  String upload(File picture, String targetPath);

  /**
   * 删除指定URL的图片文件
   *
   * @param pictureUrl 图片文件的访问URL
   * @return 删除成功返回true，否则返回false
   */
  boolean delete(String pictureUrl);
}
