package com.cykj.psychology.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 成功（带数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    // 成功（带消息和数据）
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    // 成功（只有消息）
    public static <T> Result<T> success(String msg) {
        return new Result<>(200, msg, null);
    }

    // 失败
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    // 失败（自定义错误码）
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}
