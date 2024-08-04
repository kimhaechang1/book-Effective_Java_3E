package com.khc.practice.effectivejava.ch05.item33;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Practice {

        <T> T getObject(Class<T> clazz) throws InstantiationException, IllegalAccessException {
            // 컴파일러가 타입추론을 통해 작성자의 의도를 파악하고, 작성자는 알아차리지 못하도록 캐스팅 코드를 집어넣어 작동에 문제가없게 만든다.
            return clazz.newInstance();
        }

        <T> Object getObj(Class<T> clazz) throws InstantiationException, IllegalAccessException {
            // 컴파일러가 타입추론을 할 만한것이 없다.
            // Object를 반환하는것이 전부고 반환받는 변수의 타입은 String이므로 하위타입 변수에 상위타입 객체를 할당할 수 없다.
            return clazz.newInstance();
        }


        static <T extends Annotation> T getAnnotation(Class<T> annotationType) throws InstantiationException, IllegalAccessException {
            return annotationType.newInstance();
        }


        public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException {

            // 의문은 다음과같이 시작한다.
            // 타입을 동적으로 인식하여 타입에 맞는 인스턴스를 뱉는 도구 혹은 메서드를 만들 수 없을까?
            // 예를들어 String TypeToken을 넘기면 String인스턴스를 뱉고, Object TypeToken을 던지면 Object 인스턴스를 뱉는...
            // 방법은 TypeToken을 활용하면 된다. 타입토큰이란 class 리터럴을 의미하며 X.class는 Class<X> 로 대응된다.
            Practice p = new Practice();
    //        String s = p.getObject(String.class);
    //        String s = p.getObj(String.class);

            Map<String, Integer> map = new HashMap<>(); // Java7에 추가된 다이아몬드

            // 위의 맵은 사실 map을 Raw타입으로 강제 캐스팅하면 아무런 타입이나 다 넣을 수 있다.
            Map unsafeMap = (Map) map;
            unsafeMap.put(12, "엄");
//            Integer result = map.get(12);

            Map<String, Integer> safeMap = Collections.checkedMap(map, String.class, Integer.class);
            Map map2 = (Map) safeMap;
//            map2.put(12, "엄"); // 바로 막히게된다.
//            map2.get(12);



        }
    }
