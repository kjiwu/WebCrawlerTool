package com.starter.wulei.webcrawlertool.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wulei on 2017/2/23.
 */

public class CookBook implements Serializable{
    int id;
    String title; //标题
    String pic_path; //大图路径
    String intro; //介绍
    CookingMaterial material; //烹饪材料
    List<CookingStep> steps; //烹饪步骤
    List<String> completedPics; //成品图

    public List<String> getCompletedPics() {
        return completedPics;
    }

    public void setCompletedPics(List<String> completedPics) {
        this.completedPics = completedPics;
    }

    List<String> tips; //小窍门

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(String pic_path) {
        this.pic_path = pic_path;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public CookingMaterial getMaterial() {
        return material;
    }

    public void setMaterial(CookingMaterial material) {
        this.material = material;
    }

    public List<CookingStep> getSteps() {
        return steps;
    }

    public void setSteps(List<CookingStep> steps) {
        this.steps = steps;
    }

    public List<String> getTips() {
        return tips;
    }

    public void setTips(List<String> tips) {
        this.tips = tips;
    }
}
