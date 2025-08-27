package com.ezhixuan.codeCraftAi_backend.core.saver;

import cn.hutool.core.io.FileUtil;
import com.ezhixuan.codeCraftAi_backend.ai.model.AiChatHtmlResDto;
import com.ezhixuan.codeCraftAi_backend.ai.model.enums.CodeGenTypeEnum;
import com.ezhixuan.codeCraftAi_backend.exception.BusinessException;
import com.ezhixuan.codeCraftAi_backend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.util.StringUtils.hasText;

/**
 * HTML文件保存器 专门用于保存HTML代码内容到文件的实现类 继承自文件保存模板类，实现HTML代码的保存逻辑
 *
 * @author ezhixuan
 * @version 0.0.1beta
 */
@Slf4j
public class HtmlFileSaver extends FileSaverTemplate<AiChatHtmlResDto> {
  /**
   * 获取代码类型 返回HTML代码生成类型枚举
   *
   * @return HTML代码生成类型枚举
   */
  @Override
  protected CodeGenTypeEnum getCodeType() {
    return CodeGenTypeEnum.HTML;
  }

  /**
   * 执行HTML代码保存操作 将HTML代码写入到指定的基础目录路径中
   *
   * @param resDto HTML聊天结果对象，包含HTML代码内容
   * @param baseDirPath 基础目录路径，用于保存文件
   */
  @Override
  protected void doSave(AiChatHtmlResDto resDto, String baseDirPath) {
    FileUtil.writeUtf8String(resDto.getHtmlCode(), baseDirPath);
    log.info("保存文件成功: {}", baseDirPath);
  }

  /**
   * 验证HTML结果数据 调用父类验证方法，并额外验证HTML代码内容是否为空
   *
   * @param resDto HTML聊天结果对象
   * @throws BusinessException 当HTML内容为空时抛出业务异常
   */
  @Override
  protected void validate(AiChatHtmlResDto resDto) {
    super.validate(resDto);
    if (!hasText(resDto.getHtmlCode())) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "html 内容不能为空");
    }
  }
}
