package com.khc.practice.effectivejava.ch02.item2.telescoping_constructor_pattern;

public class NutritionFacts {

    // telescoping_constructor_pattern: 점층적 생성자 패턴
    // 필요한 매게변수의 개수가 1개인 생성자 부터 N개인 생성자 개수까지 점층적으로 늘려가며 생성자를 만든다.

    private final int servingSize;  // (ml, 1회 제공량)
    private final int servings;     // (회, 총 n회 제공량)
    private final int calories;     // (1회 제공량당)
    private final int fat;          // (g/1회 제공량)
    private final int sodium;       // (mg/1회 제공량)
    private final int carbohydrate; // (g/1회 제공량)

    public NutritionFacts(int servingSize, int servings){
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories){
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat){
        this(servingSize, servings, calories, fat, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium){
        this(servingSize, servings, calories, fat, sodium, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate){
        this.servingSize    = servingSize;
        this.servings       = servings;
        this.calories       = calories;
        this.fat            = fat;
        this.sodium         = sodium;
        this.carbohydrate   = carbohydrate;
    }

}
