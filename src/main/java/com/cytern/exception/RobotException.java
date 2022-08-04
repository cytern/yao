package com.cytern.exception;

/**
 * 机器人异常
 */
public class RobotException extends RuntimeException{
    private Integer code;

    private String msg;


    public RobotException(String message) {
        super(message);
    }

    public RobotException(RuntimeException e) {
        super(e.getMessage());
    }


    public RobotException(Integer code,String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
