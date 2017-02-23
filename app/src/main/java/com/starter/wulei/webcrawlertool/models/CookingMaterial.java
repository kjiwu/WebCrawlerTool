package com.starter.wulei.webcrawlertool.models;

import java.util.List;
import java.util.UUID;

/**
 * Created by wulei on 2017/2/23.
 */

public class CookingMaterial {
    UUID uuid;
    String difficulty; //烹饪难度
    String cookingTime; //烹饪时间
    List<MaterialInfo> mainMaterials; //主要食材
    List<MaterialInfo> ingredients; //辅助食材

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public List<MaterialInfo> getMainMaterials() {
        return mainMaterials;
    }

    public void setMainMaterials(List<MaterialInfo> mainMaterials) {
        this.mainMaterials = mainMaterials;
    }

    public List<MaterialInfo> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<MaterialInfo> ingredients) {
        this.ingredients = ingredients;
    }
}
