package com.cytern.pojo.response;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RobotResponse {
    /**
     * 状态码
     */
    private Boolean code;
    /**
     * 提示
     */
    private String message;

    /**
     * 值
     */
    private HashMap<String,Object> data;


    public Boolean getCode() {
        return code;
    }

    public void setCode(Boolean code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
