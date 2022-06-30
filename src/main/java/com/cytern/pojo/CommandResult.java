package com.cytern.pojo;

import com.alibaba.fastjson.JSONObject;

public class CommandResult {
    private Integer result;
    private String returnMsg;


    public static CommandResult success() {
        return new CommandResult();
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }
}
