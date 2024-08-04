package com.khc.practice.effectivejava.ch05.item33;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TypesafeMap {

    private final Map<Class<?>, Object> map = new HashMap<>();
    // 타입토큰을 함께 저장하여 다양한 타입의 정보를 기입하려 한다.


    public void unsafePut(Class<?> clazz, Object value){

        map.put(clazz, value);
    }

    public <T> void put(Class<T> clazz, T value){
        map.put(clazz, value);
    }

    public <T> T unsafeGet(Class<T> clazz){
        // 인스턴스 검사 없이 명시적인 T타입으로의 타입캐스팅은 안전하지 않을수도 있다.
        return (T) map.get(clazz);
    }

    public <T> T get(Class<T> clazz){
        // cast 메소드를 사용하면 해당 인스턴스 검사를 통해 RuntimeException을 일으키거나 그게 아니라면 캐스팅을 해준다.
        // 사실 아래의 cast 메소드가 해주는게 크게 다를건 없다. 뭔진 모르겠으나 isInstance() 메소드가 확신을 주는 메소드가 아닐까 싶다.
        return clazz.cast(map.get(clazz));
    }

    public void causeByUnsafePut(){
        Class<Integer> token = Integer.class;
        unsafePut(token, "44"); // 이런게 가능해버린다.
        Integer value = unsafeGet(token);
        // 타입토큰의 정보와 제네릭 타입의 정보가 일치하지 않아서 의도한 대로 동작하지 않는다.
        System.out.println(value);
    }

    public void useSafe(){
        Class<?> [] classes = new Class[]{String.class, Integer.class, List.class};
        put(String.class, "김회창");
        put(Integer.class, 100);
        put(List.class, List.of(10,20,30));

        for(Class<?> clazz: classes){
            System.out.println(get(clazz));
        }
    }




    public static void main(String[] args) {
        TypesafeMap map = new TypesafeMap();
    }
}
