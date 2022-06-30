package com.cytern.pojo;

import com.alibaba.fastjson.JSONArray;

/**
 * 简单图片拆分
 */
public class SimpleImageSub {
    private Integer row;
    private Integer col;
    private JSONArray index;

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getCol() {
        return col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    public JSONArray getIndex() {
        return index;
    }

    public void setIndex(JSONArray index) {
        this.index = index;
    }

    public SimpleImageSub() {

    }

    public SimpleImageSub(Integer row, Integer col) {
        this.row = row;
        this.col = col;
    }
}
