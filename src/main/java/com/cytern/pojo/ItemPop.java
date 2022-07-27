package com.cytern.pojo;

public class ItemPop {

    /**
     * 扭蛋功能属性 用于随机的权重 最终概率为所有物品权重总和与该物品权重之比
     */
    private Integer randomWeight;

    /**
     * 扭蛋功能属性 强制出现概率 填有次属性会保证每次随机获得时强制进行一次随机 如果随机结果为真直接返回该值
     */
    private Integer forceChance;
}
