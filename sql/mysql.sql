create database if not exists `code_craft_ai_db`;

use code_craft_ai_db;

/*用户模块*/
/*用户表*/
-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `password`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
    `account`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
    `name`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '用户名',
    `avatar`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '头像地址',
    `profile`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '个人简介',
    `email`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '邮箱',
    `status`      tinyint                                                       NOT NULL DEFAULT '1' COMMENT '状态：1-正常，0-禁用',
    `create_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     tinyint                                                       NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    `role`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户角色',
    PRIMARY KEY (`id`),
    KEY `idx-name` (`name`) USING BTREE COMMENT '用户名称索引',
    KEY `idx-account` (`account`) USING BTREE COMMENT '用户账号索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for sys_app
-- ----------------------------
DROP TABLE IF EXISTS `sys_app`;
CREATE TABLE `sys_app`
(
    `id`            bigint                                  NOT NULL AUTO_INCREMENT COMMENT '主键 id',
    `name`          varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名称',
    `cover`         varchar(255) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '应用封面',
    `init_prompt`   text COLLATE utf8mb4_unicode_ci         NOT NULL COMMENT '初始化提示词',
    `code_gen_type` varchar(64) COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '生成类型枚举',
    `deploy_key`    varchar(64) COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '部署标识',
    `deploy_time`   datetime                                         DEFAULT NULL COMMENT '部署时间',
    `priority`      int                                     NOT NULL DEFAULT '0' COMMENT '展示优先级',
    `user_id`       bigint                                  NOT NULL COMMENT '所属用户 id',
    `create_time`   datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       tinyint                                 NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    `first_chat`    tinyint                                 NOT NULL DEFAULT '1' COMMENT '首次对话',
    PRIMARY KEY (`id`, `name`, `user_id`),
    UNIQUE KEY `uq-deploy` (`deploy_key`) USING BTREE COMMENT '部署标识唯一',
    KEY `idx-name` (`name`) USING BTREE COMMENT '应用名称索引',
    KEY `idx-userId` (`user_id`) USING BTREE COMMENT '用户 id 索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for sys_chat_history
-- ----------------------------
DROP TABLE IF EXISTS `sys_chat_history`;
CREATE TABLE `sys_chat_history`
(
    `id`           bigint                                 NOT NULL AUTO_INCREMENT COMMENT 'id',
    `message`      text COLLATE utf8mb4_unicode_ci        NOT NULL COMMENT '消息',
    `message_type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'user/ai',
    `app_id`       bigint                                 NOT NULL COMMENT '应用id',
    `user_id`      bigint                                 NOT NULL COMMENT '创建用户id',
    `create_time`  datetime                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime                               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      tinyint                                NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_appId` (`app_id`),
    KEY `idx_createTime` (`create_time`),
    KEY `idx_appId_createTime` (`app_id`, `create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='对话历史';
