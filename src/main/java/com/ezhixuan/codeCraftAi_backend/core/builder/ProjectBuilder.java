package com.ezhixuan.codeCraftAi_backend.core.builder;

/**
 * 项目构建器接口
 * <p>
 * 该接口定义了项目构建的基本功能，用于构建和部署生成的代码项目。
 * 实现类需要提供具体的构建逻辑，支持同步和异步构建方式。
 * </p>
 *
 * @author ezhixuan
 * @since 2025-08-26
 */
public interface ProjectBuilder {

    /**
     * 构建项目
     *
     * @param buildPath 构建路径，指定项目构建的目标目录
     * @param async     是否异步构建，true表示异步构建，false表示同步构建
     * @return 构建结果，true表示构建成功，false表示构建失败
     */
    boolean build(String buildPath, boolean async);
}