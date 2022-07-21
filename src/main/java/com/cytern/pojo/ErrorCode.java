package com.cytern.pojo;

public enum ErrorCode {
    PARAM_SPLICE_NOT_RIGHT ("解析参数错误",101),

    NET_WORK_RETURN_ERROR ("爻图片解析失败 无法寻找正确的路径",102),
    IMAGE_UPLOAD_CANT_FOUND_SOURCE ("爻图片解析失败 资源路径并非正确的三级路径",9991),
    IMAGE_UPLOAD_CANT_FOUND_PATH ("爻图片解析失败 无法寻找正确的路径",9992);



    private final String msg;
    private final Integer code;
    ErrorCode(String msg,Integer code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg + code;
    }

    public String getMsg(Integer addCode) {
        return msg + code + addCode;
    }
}
