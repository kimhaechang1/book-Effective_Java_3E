package com.khc.practice.effectivejava.ch02.item2.telescoping_constructor_pattern;

public class Practice {
    public static void main(String[] args) {

        NutritionFacts nutritionFacts = new NutritionFacts(240, 8, 100, 0, 35, 27);
        // 사실상 사용하지 않는 값에 대해서 0을 넣어서 해결하는데
        // 이 클래스를 사용하여 객체를 만들어내는 사용자 입장에서는 매게변수 길이만큼 헷갈릴 요소가 늘어나고
        // 각 매게변수의 역할을 인지하지 못한체 사용하여 컴파일러가 인지하지 못하는 오류가 발생할 수 도 있다.
    }
}
