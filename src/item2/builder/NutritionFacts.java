package com.khc.practice.effectivejava.ch02.item2.builder;

public class NutritionFacts {

    // telescoping_constructor_pattern의 장점과 javabeans_pattern의 장점을 섞은 빌더 패턴
    // 빌더 패턴을 사용하면 불변 객체를 만들 수 있고, 필수 매개변수와 선택 매개변수를 사용자가 인지하고 객체의 필드를 채울 수 있다.

    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder{
        private final int servingSize;  // 필수 매개변수
        private final int servings;     // 필수 매개변수


        // 선택 매개변수는 기본값으로 초기화 한다.
        private int calories        = 0;
        private int fat             = 0;
        private int sodium          = 0;
        private int carbohydrate    = 0;

        public Builder(int servingSize, int servings){
            this.servingSize    = servingSize;
            this.servings       = servings;
        }

        public Builder calories(int val){
            calories = val;
            return this;
        }
        public Builder fat(int val){
            fat = val;
            return this;
        }
        public Builder sodium(int val){
            sodium = val;
            return this;
        }
        public Builder carbohydrate(int val){
            carbohydrate = val;
            return this;
        }

        public NutritionFacts build(){
            return new NutritionFacts(this);
        }
    }
    private NutritionFacts(Builder builder){
        servingSize     = builder.servingSize;
        servings        = builder.servings;
        calories        = builder.calories;
        fat             = builder.fat;
        sodium          = builder.sodium;
        carbohydrate    = builder.carbohydrate;
    }

}
