package com.khc.practice.effectivejava.ch02.item2.builder;

public class Practice {
    public static void main(String[] args) throws Exception{
        NutritionFacts nutritionFacts = new NutritionFacts.Builder(240, 8)
                .calories(100).sodium(35).carbohydrate(27).build();

        // 마치 메소드들을 연쇄적으로 호출한다 하여 Fluent API라고 한다.

        NyPizza pizza = new NyPizza.Builder(NyPizza.Size.SMALL)
                .addTopping(Pizza.Topping.SAUSAGE).addTopping(Pizza.Topping.ONION).build();

        Calzone calzone = new Calzone.Builder()
                .addTopping(Pizza.Topping.HAM).sauceInside().build();

    }
}
