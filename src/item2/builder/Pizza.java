package com.khc.practice.effectivejava.ch02.item2.builder;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public abstract class Pizza {

    public enum Topping { HAM, MUSHROOM, ONION, PEPPER, SAUSAGE }

    final Set<Topping> toppings;

    abstract static class Builder<T extends Builder<T>> {

        // 하위 타입 빌더 객체를 제네릭으로 넘겨받는 방식
        // 상한 경계를 둔 이유는, 다형성을 최대한 활용하기 때문에도 있고,
        // 궁극적인 목적은 self() 메서드에 달려있다.

        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);
        public T addTopping(Topping topping){
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        public abstract Pizza build();

        // 하위 클래스는 이 메서드를 재정의 하여
        // this를 반환하도록 해야 한다.

        protected abstract T self();
        // return this로 하지않고 self() 로 별도로 만든 이유는
        // 하위타입 구현객체가 부모 객체의 메소드를 호출하고서 Builder를 반환할 때, 그때 하위타입 객체 Builder로 이어져야 하므로
        // return this를 하게되면 부모타입 객체가 반환되어져서, 부모타입 메서드만 호출이 가능하게 된다.
    }
    Pizza(Builder<? extends Pizza.Builder> builder){
        toppings = builder.toppings.clone();
        // 복사하는 이유
        // 멀티 스레드 환경에서 불변식을 검사하기위해 준비중인 builder 객체속 topping 객체에 대해서
        // 그 찰나의 순간에 다른 스레드가 동일한 toppings 객체에 대해 수정이 발생할 수 있기 때문에
        // 그리고 clone 메소드의 경우 신중하게 사용해야 한다.
        // toppings는 Enum클래스이기 때문에 final 클래스여서 더이상 상속할 수 없다는 특징이 있으니깐 clone을 호출해도 상관없지만
        // 만약 다른 하위타입으로 상속할 여지가 있다는것은 적어도 1회이상 clone을 재정의했을 가능성이 있다는 것이므로, clone보다는 직접 방어적 복사를 하는게 좋다.
    }
}
