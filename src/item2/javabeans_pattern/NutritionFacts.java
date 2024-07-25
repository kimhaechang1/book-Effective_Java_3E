package com.khc.practice.effectivejava.ch02.item2.javabeans_pattern;

public class NutritionFacts {

    // javabeans_pattern: 배개변수가 없는 생성자로 만든 후, setter 매서드를 호출해서 원하는 매개변수의 값을 설정하는 방식

    private int servingsSize    = -1;
    private int servings        = -1;
    private int calories        = 0;
    private int fat             = 0;
    private int sodium          = 0;
    private int carbohydrate    = 0;

    public NutritionFacts() {}

    public void setServingsSize(int servingsSize) {
        this.servingsSize = servingsSize;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }

    public void setCarbohydrate(int carbohydrate) {
        this.carbohydrate = carbohydrate;
    }
}
