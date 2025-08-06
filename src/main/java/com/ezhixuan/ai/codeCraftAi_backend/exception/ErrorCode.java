package com.ezhixuan.ai.codeCraftAi_backend.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 成功
    SUCCESS(0, "ok"),

    // 客户端错误 (4xx)
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    TOO_MANY_REQUEST(42900, "请求过于频繁"),

    // 服务器错误 (5xx)
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    DATABASE_ERROR(50002, "数据库错误"),
    NETWORK_ERROR(50003, "网络异常"),
    FILE_UPLOAD_ERROR(50004, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(50005, "文件下载失败"),
    EXTERNAL_API_ERROR(50006, "外部接口调用失败"),
    CONFIG_ERROR(50007, "配置错误"),
    CACHE_ERROR(50008, "缓存异常"),
    MESSAGE_QUEUE_ERROR(50009, "消息队列异常");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
