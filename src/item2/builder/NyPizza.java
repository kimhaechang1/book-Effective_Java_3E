package com.khc.practice.effectivejava.ch02.item2.builder;

import java.util.Objects;

public class NyPizza extends Pizza{

    public enum Size { SMALL, MEDIUM, LARGE }
    private final Size size;

    public static class Builder extends Pizza.Builder<Builder>{

        private final Size size; // 필수값이므로 빌더 생성자에서 바로 호출되도록 한다.

        public Builder(Size size){
            this.size = Objects.requireNonNull(size);
        }

        @Override
        public NyPizza build() {
            return new NyPizza(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private NyPizza(Builder builder){
        super(builder);
        this.size = builder.size;
    }
}
