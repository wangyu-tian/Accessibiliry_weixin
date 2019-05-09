package com.suspen.wangyu.model;

/**
 * @Author wangyu
 * @Description: Copyright yiYuan Networks 上海义援网络科技有限公司. All rights reserved.
 * @Date 2019/4/2
 */
public class SizeEntity {
    private int width;
    private int height;

    public SizeEntity(){}
    public SizeEntity(int width,int height){
        setWidth(width);
        setHeight(height);
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}