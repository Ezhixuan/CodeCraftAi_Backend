package com.ezhixuan.codeCraftAi_backend.manager.oss;

import java.io.InputStream;

/**
 * 对象存储服务接口 定义了上传、下载、删除等操作的统一接口
 *
 * @version 0.0.2beta
 * @author ezhixuan
 */
public interface ObjectStorageService {

  /**
   * 获取当前使用的上传模式
   *
   * @return UploadModel 上传模式枚举值
   */
  OssModelEnum getUploadModel();

  /**
   * 上传文件
   *
   * @param inputStream 文件输入流
   * @param targetPath 目标存储路径
   * @return String 上传后的文件访问URL
   */
  String doUpload(InputStream inputStream, String targetPath);

  /**
   * 下载文件
   *
   * @param urlStr 文件的URL地址
   * @return InputStream 图片文件输入流
   */
  InputStream doDownload(String urlStr);

  /**
   * 删除文件
   *
   * @param urlStr 文件的URL地址
   * @return boolean 删除是否成功
   */
  boolean doDelete(String urlStr);
}
