package com.khc.practice.effectivejava.ch05.item33;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class SuperTypeToken {

    // 슈퍼 타입 토큰이란? 예를들어 String이나 일반 제네릭없는 타입에 대한 타입토큰을 활용해서 타입 안전 이종컨테이너를 구현할 수 있다.
    // 하지만 List<String>과 같은 경우, 제네릭 속 타입을 얻기 힘들다. 왜냐하면 제네릭은 타입소거에 의해 제거되기 때문

    // 이러한 것을 타입이 보존되는 원리와 리플렉션을 활용해서 가능토록 만들 수 있다.
    // 어떤 제네릭 클래스의 타입하나를 확정지은 클래스를 확장한 서브클래스로 부터 제네릭 정보를 받을 수 있기 때문이다.

    static class Generic<T>{
        T value;
    }

    static class Sub extends Generic<List<Integer>>{

    }

    static void getGenericType(){
        Sub sub = new Sub();
        Type type = sub.getClass().getGenericSuperclass();
        ParameterizedType pType = (ParameterizedType)type;
        Type genericType = pType.getActualTypeArguments()[0];
        System.out.println(genericType);
    }

    static void getGenericTypeByAnony(){
        Generic generic = new Generic<List<Integer>>() {};
        Type type = generic.getClass().getGenericSuperclass();
        ParameterizedType pType = (ParameterizedType) type;
        Type genericType = pType.getActualTypeArguments()[0]; // 물론 타입 파라미터가 하나라서 이렇게 해준다.
        System.out.println(genericType);
    }

    // 이와같은 원리를 사용하여 TypeReference라는 클래스를 하나 정의한다.
    // TypeReference의 역할은 제네릭 인자로 들어온 타입정보를 필드에 저장해놓는것이다.
    // 중요한점은 실질적인 역할을 수행할 녀석은 익명클래스라는 점이다.

    static class TypeReference<T> {
        // 여기서 T의 역할이 익명 클래스에서 슈퍼클래스의 타입정보를 유지시키기 위함이다.
        Type type;

        public TypeReference(){
            Type sType = this.getClass().getGenericSuperclass();
            // 익명 클래스의 인스턴스의 슈퍼클래스는 TypeReference<T> 에서 T가 무엇이든 고정되있을것
            // 만약 TypeReference 클래스를 그대로 인스턴스화 하려고 하면 막아야 한다.
            // 무조건 익명 클래스로 활용하게 만들어야 한다.
            if(sType instanceof ParameterizedType){
                this.type = ((ParameterizedType) sType).getActualTypeArguments()[0];
            }
            // ParameterizedType이 아니란 얘기는 즉, 익명 구현클래스를 만들지 않았단것
            else throw new RuntimeException();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            // 여기서 문제가 발생한다.
            // 언제나 익명 클래스를 만든다는걸 잊으면 안된다.
            if (o == null || getClass().getSuperclass() != o.getClass().getSuperclass()) return false;
            TypeReference<?> that = (TypeReference<?>) o;
            return Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type);
        }
    }

    static class TypesafeMap {

        private final Map<TypeReference<?>, Object> map = new HashMap<>();

        public <T> void put(TypeReference<T> tr, T value) {
            map.put(tr, value);
        }

        public <T> T get(TypeReference<T> tr){
            // 여기서 이제 TypeReference<T>를 상속한 클래스가 갖고있는 type 필드를 통해 캐스팅 해주어야 한다.
            // 그런데 null이 나올것이다. 왜냐하면 TypeReference<T>를 사용하는것이 아니라, 매번 새로운 익명 클래스를 만들기 때문
            // 그래서 컬렉션에서 동등성 문제가 발생하게 된다.

            // 그런데 여전히 중첩되어있는 List<String>의 경우에서 클래스정보를 못 얻어오고 있다
            // 왜냐하면 아래의 캐스팅은 사실 tr.type이 클래스토큰으로 얻어올 수 있다고 판단하기 때문이다.
            // 하지만 List<String>과 같은 경우에서는 tr.type이 ParameterizedType이 된다.
            // 따라서 ParameterizedType인지 아닌지 검사해야 한다.
            // return ((Class<T>)tr.type).cast(map.get(tr));
            if (tr.type instanceof Class<?>){
                return ((Class<T>)tr.type).cast(map.get(tr));
            } else {
                return ((Class<T>)((ParameterizedType) tr.type).getRawType()).cast(map.get(tr));
            }
        }
    }


    public static void main(String[] args) {
        getGenericTypeByAnony();
        TypesafeMap typesafeMap = new TypesafeMap();
        typesafeMap.put(new TypeReference<Integer>() {}, 12);
        typesafeMap.put(new TypeReference<String>() {}, "김회창");
        typesafeMap.put(new TypeReference<List<Integer>>() {}, Arrays.asList(19, 2, 11));

        Integer value1 = typesafeMap.get(new TypeReference<Integer>() {});
        String value2 = typesafeMap.get(new TypeReference<String>() {});
        List<Integer> list = typesafeMap.get(new TypeReference<List<Integer>>(){});

        System.out.println(value1);
        System.out.println(value2);
        System.out.println(list);
    }

}
