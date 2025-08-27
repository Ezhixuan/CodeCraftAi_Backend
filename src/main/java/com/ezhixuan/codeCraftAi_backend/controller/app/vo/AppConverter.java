package com.ezhixuan.codeCraftAi_backend.controller.app.vo;

import com.ezhixuan.codeCraftAi_backend.domain.entity.SysApp;

import cn.hutool.core.bean.BeanUtil;

public abstract class AppConverter {

  public SysApp toEntity() {
    return BeanUtil.copyProperties(this, SysApp.class);
  }

  public void build(SysApp sysApp) {
    BeanUtil.copyProperties(sysApp, this);
  }
}
