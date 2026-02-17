package com.example.springboot.common;

import lombok.Data;

@Data
//同意对象
public class Result {
//    状态码，返回成功还是失败,一般“200”成功，500错误
    private int code;

    private static final int SUCCESS_CODE = 200;
    private static final int ERROR_CODE = 500;

//    错误信息
    private String msg;
//    所有类的一个继类
    private Object data;

    public static Result success() {
        Result result = new Result();
        result.setCode(SUCCESS_CODE);
        result.setMsg("success");
        return result;
    }
    public static Result success(Object data) {
        Result result = new Result();
        result.setData(data);
        return result;
    }
    public static Result error() {
        Result result = new Result();
        result.setCode(ERROR_CODE);
        result.setMsg("error");
        return result;
    }
    public static Result error(int code, String msg) {
        Result result = new Result();
        result.setCode(code);
        return result;
    }
}
