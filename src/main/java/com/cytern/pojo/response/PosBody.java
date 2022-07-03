package com.cytern.pojo.response;

/**
 * 好感度请求返回体
 */
public class PosBody extends RobotResponse{
    /**
     * 好感度
     */
    private Integer level;

    /**
     * 备注
     */
    private String mark;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
