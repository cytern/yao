package com.cytern.pojo;

import com.alibaba.fastjson.JSONArray;

/**
 * 简单图片拆分
 */
public class SimpleImageSub {
    private Integer xNum;
    private Integer yNum;
    private JSONArray index;

    public Integer getxNum() {
        return xNum;
    }

    public void setxNum(Integer xNum) {
        this.xNum = xNum;
    }

    public Integer getyNum() {
        return yNum;
    }

    public void setyNum(Integer yNum) {
        this.yNum = yNum;
    }

    public JSONArray getIndex() {
        return index;
    }

    public void setIndex(JSONArray index) {
        this.index = index;
    }

    public SimpleImageSub() {

    }

    public SimpleImageSub(Integer xNum, Integer yNum) {
        this.xNum = xNum;
        this.yNum = yNum;
    }
}
