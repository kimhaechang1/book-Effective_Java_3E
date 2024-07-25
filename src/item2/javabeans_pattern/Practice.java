package com.khc.practice.effectivejava.ch02.item2.javabeans_pattern;

public class Practice {
    public static void main(String[] args) {
        NutritionFacts nutritionFacts = new NutritionFacts();
        // javabeans_pattern 이기 때문에 default 생성자를 통해 우선 파라미터를 초기화 해준다.

        nutritionFacts.setServingsSize(240);
        nutritionFacts.setServings(8);
        nutritionFacts.setCalories(100);
        nutritionFacts.setSodium(35);
        nutritionFacts.setCarbohydrate(27);

        // 이전 telescoping_constructor_pattern 보다는 사용자 입장에서 필요한 필드를 채우는데 있어서 헷갈릴 요소가 없다.
        // 하지만 더이상 불변 객체를 만들 수 없어졌다. 즉, field가 더이상 final이 아니기 때문에 수정의 여지가 생기고
        // 여러번의 setter를 호출해야 비로소 일관성이 생기기에 쉽게 오염될 수 있다.

    }
}
