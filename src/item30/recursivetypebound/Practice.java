package com.khc.practice.effectivejava.ch05.item30.recursivetypebound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Practice {

    public static void main(String[] args) {
       AsiaMoney asiaMoney =  new AsiaMoney.Builder()
               .setName("김회창")
               .setValue(1245)
               .addCountry("한국")
               .addCountry("중국")
               .addCountry("일본")
               .build();
       System.out.println(asiaMoney);

    }
}

class Moneys{
    private Moneys(){

    }
    private static final List<Money> collection = new ArrayList<>();

    public static <T extends Money> List<T> getSingletonGenericMoneyFactory(){
        return (List<T>) collection;
    }

    public static <E extends Comparable<E>> E max(Collection<E> c){
        if(c.isEmpty()){
            throw new IllegalArgumentException();
        }

        E result = null;
        for (E e: c){
            if (result == null || e.compareTo(result) > 0){
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }
}

class Money implements Comparable<Money>{
    int value;
    String name;

    @Override
    public int compareTo(Money o) {
        return this.value - o.value;
    }

    abstract static class Builder<T extends Builder<T>>{

        // abstract로 한 이유는, 다른 Money를 상속받는 클래스에서 편하게 Builder를 구현하기 위해서
        // 코드 재사용성 측면
        private int value;

        private String name;

        public T setName(String name){
            this.name = name;
            // T타입으로 한 이유는, 메소드자체는 부모 메소드지만,
            // 하위 클래스로부터 넘겨받은 Builder 타입으로 이어져야 하므로 -> 체이닝 때문
            return self();
        }

        public T setValue(int value){
            this.value = value;
            return self();
        }

        public abstract Money build();
        // 빌드메소드는 하위타입에서 넘겨받은 타입으로 최종 객체를 만들어낸다.
        protected abstract T self();
    }

    Money(Builder<?> builder) {
        this.name = builder.name;
        this.value = builder.value;

    }
}

class AsiaMoney extends Money{

    private List<String> countries = new ArrayList<>();

    static class Builder extends Money.Builder<Builder>{

        private final List<String> countries = new ArrayList<>();

        public Builder addCountry(String name){
            countries.add(name);
            return this;
        }

        public AsiaMoney build() {
            return new AsiaMoney(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    AsiaMoney(Builder builder) {
        super(builder);
        countries = builder.countries;
    }

    public String toString(){
        return "[ name: "+name+" value: "+value+" country list: "+countries+" ]";
    }

}

//class Dolar extends Money{
//
//    public Dolar(int value) {
//        super(value);
//    }
//
//    public String toString(){
//        return "[ value: "+this.value +" $ ]";
//    }
//}
//
//class Won extends Money{
//
//
//    public Won(int value) {
//        super(value);
//    }
//    public String toString(){
//        return "[ value: "+this.value +" 원 ]";
//    }
//}
