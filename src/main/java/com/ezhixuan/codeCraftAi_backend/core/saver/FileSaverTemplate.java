package com.ezhixuan.codeCraftAi_backend.core.saver;

import cn.hutool.core.io.FileUtil;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import com.ezhixuan.codeCraftAi_backend.utils.PathUtil;

import java.io.File;
import java.util.Objects;

/**
 * 文件保存模板抽象类 定义文件保存的通用流程和接口，使用模板方法模式实现 子类需要实现具体的保存逻辑
 *
 * @author ezhixuan
 * @version 0.0.1beta
 * @param <T> AI聊天结果对象的类型
 */
public abstract class FileSaverTemplate<T> {

  /**
   * 保存文件的模板方法 定义文件保存的通用流程：验证数据 -> 构建基础目录路径 -> 执行具体保存 -> 返回文件对象
   *
   * @param saverDto 文件保存数据传输对象
   * @return 保存的文件目录对象
   */
  public final File save(FileSaverDto saverDto) {
    T aiChatDto = (T) saverDto.getAiChatDto();
    Long appId = saverDto.getAppId();

    validate(aiChatDto);
    String baseDirPath = PathUtil.buildPath(PathUtil.ORIGINAL_DIR, getCodeType(), appId);
    doSave(aiChatDto, baseDirPath);
    return FileUtil.file(baseDirPath);
  }

  /**
   * 获取代码类型抽象方法 由子类实现，返回具体的代码生成类型枚举
   *
   * @return 代码生成类型枚举
   */
  protected abstract CodeGenTypeEnum getCodeType();

  /**
   * 执行具体保存操作的抽象方法 由子类实现具体的文件保存逻辑
   *
   * @param resDto AI聊天结果对象
   * @param baseDirPath 基础目录路径
   */
  protected abstract void doSave(T resDto, String baseDirPath);

  /**
   * 验证数据 检查AI聊天结果对象是否为空，如果为空则抛出业务异常
   *
   * @param resDto AI聊天结果对象
   * @throws BusinessException 当结果对象为空时抛出业务异常
   */
  protected void validate(T resDto) {
    if (Objects.isNull(resDto)) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "未接收到结果");
    }
  }
}
