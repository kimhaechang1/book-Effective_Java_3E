package com.khc.practice.effectivejava.ch02.item2.builder;

public class Calzone extends Pizza{

    private final boolean sauceInside;

    public static class Builder extends Pizza.Builder<Builder>{

        // 여기서 제네릭 타입 인자로 넘겨주는 Builder는 Calzone.Builder 라는걸 잊으면 안됨

        private boolean sauceInside = false;    // 필수 타입이 아니므로, 기본값을 부여하기 위해

        public Builder sauceInside(){
            this.sauceInside = true;
            return this;
        }

        // 하위타입 빌더를 상위타입으로 넘김으로서, 상위타입의 메소드를 하위타입의 객체에 실어담을 수 있다.
        @Override
        public Calzone build() {
            return new Calzone(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
    private Calzone(Builder builder){
        super(builder);
        // 부모 타입 객체의 필드를 채우는 용도로 넘기고
        // 자식 객체의 필드를 채우는건 별도로 진행
        this.sauceInside = builder.sauceInside;
    }
}
