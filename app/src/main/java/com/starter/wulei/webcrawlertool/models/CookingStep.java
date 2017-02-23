package com.starter.wulei.webcrawlertool.models;

/**
 * Created by wulei on 2017/2/23.
 */

public class CookingStep {
    int order;
    String name;
    String img_path;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }
}
