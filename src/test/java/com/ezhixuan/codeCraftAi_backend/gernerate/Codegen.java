package com.ezhixuan.codeCraftAi_backend.gernerate;

import java.util.Map;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;

public class Codegen {

    public static final String[] TABLE_NAMES = {"sys_user"};

    public static void main(String[] args) {
        // 加载数据
        Dict dict = YamlUtil.loadByPath("application.yml");
        Map<String, String> datasource = dict.getByPath("spring.datasource");
        String url = datasource.get("url");
        String username = datasource.get("username");
        String password = datasource.get("password");
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        // 创建配置内容
        GlobalConfig globalConfig = createGlobalConfigUseStyle();

        // 通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfigUseStyle() {
        // 创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        // 设置根包
        globalConfig.getPackageConfig().setBasePackage("com.ezhixuan.common.common_backend.gernerate.res");

        // 设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
        globalConfig.getStrategyConfig().setGenerateTable(TABLE_NAMES).setLogicDeleteColumn("deleted");

        // 设置生成 entity 并启用 Lombok
        globalConfig.enableEntity().setWithLombok(true).setJdkVersion(21);

        // 设置生成 mapper
        globalConfig.enableMapper();
        globalConfig.enableMapperXml();

        // 设置生成 service
        globalConfig.enableService();
        globalConfig.enableServiceImpl();

        // 可以单独配置某个列
        globalConfig.getJavadocConfig().setAuthor("Ezhixuan").setSince("");
        return globalConfig;
    }
}
