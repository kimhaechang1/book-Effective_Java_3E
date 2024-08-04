## 타입 안전 이종 컨테이너

<a href="../src/item33">예제 코드</a>

이것은 "타입을 동적으로 인식하여 타입에 맞는 인스턴스를 뱉는 도구 혹은 메서드를 만들 수 없을까?"를 본떠서 시작한다.

다양한 타입의 데이터를 보관하려면 해당 데이터가 어떤 타입인지에 대한 타입토큰을 관리하여야 한다.

결국 위 타입 안전 이종 컨테이너의 핵심은 타입토큰을 통해 해당 데이터가 안전하게 저장되고 꺼내올수 있도록 보장하는 것이다.

아래는 해당하는 기능의 메소드를 구현한 예시 두가지이다.

```java
<T> T getObject(Class<T> clazz) throws InstantiationException, IllegalAccessException {
    // 컴파일러가 타입추론을 통해 작성자의 의도를 파악하고, 
    // 작성자는 알아차리지 못하도록 캐스팅 코드를 집어넣어 작동에 문제가 없게 만든다.
    return clazz.newInstance();
}

<T> Object getObj(Class<T> clazz) throws InstantiationException, IllegalAccessException {
    // 컴파일러가 타입추론을 할 만한것이 없다.
    // Object를 반환하는것이 전부고 반환받는 변수의 타입은 String이므로 하위타입 변수에 상위타입 객체를 할당할 수 없다.
    return clazz.newInstance();
}
```

사실 아래의 메소드와 위의 메소드는 컴파일러가 검사하고 난 뒤에 동일하게 `Object`타입이 된다.

하지만 위의 메소드의 경우에는 명시적인 캐스팅이 없어도되고, 아래의 경우에는 명시적인 캐스팅이 필요하게 된다.(안하면 타입 불일치 컴파일 에러가 발생한다.)

이유는 컴파일러가 타입을 추론할 수 있느냐, 그럴필요도 없이 애초에 타입이 불일치 하냐의 차이가 있다.

전자의 경우에는 개발자 입장에서는 당연히 `T -> 자기가 원하는 타입` 이 되어야 할 것이다.

하지만 실질적으로 타입소거에 의거하여 `Object`로 바뀌게 되고, 내부 인스턴스 검사를 통해 캐스팅후 값을 사용해야 할 것이다.

이러한 상황을 미리 알아차릴 수 있는 단계인 컴파일단계에서 이를 위해 캐스팅코드를 넣게 된다.

### 타입 안전 이종 컨테이너의 구현: TypesafeMap

타입 안전 이종 컨테이너를 구현하려면 Key타입과 Value타입에 대한 엄격한 검사가 필요하다.

그래서 아래와 같이 구현한다.

```java
public class TypesafeMap {

    private final Map<Class<?>, Object> map = new HashMap<>();
    // 타입토큰을 함께 저장하여 다양한 타입의 정보를 기입하려 한다.


    public void unsafePut(Class<?> clazz, Object value){
        map.put(clazz, value);
    }

    public <T> void put(Class<T> clazz, T value){
        map.put(clazz, clazz.cast(value));
    }

    public <T> T unsafeGet(Class<T> clazz){
        // 인스턴스 검사 없이 명시적인 T타입으로의 타입캐스팅은 안전하지 않을수도 있다.
        return (T) map.get(clazz);
    }

    public <T> T get(Class<T> clazz){
        // cast 메소드를 사용하면 해당 인스턴스 검사를 통해 RuntimeException을 일으키거나 그게 아니라면 캐스팅을 해준다.
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
}
```

위의 메소드를 일부러 `unsafe` 키워드가 달린것과 안달린것으로 구분하였다.

가장 큰 차이는 제네릭 메소드이냐 아니냐로 갈린다.

이는 제네릭 타입을 통해 컴파일러가 타입추론을 하여 적절하게 캐스팅을 넣을 수 있게 된다.

그런데 제네릭 메소드만으로 타입 검사를 엄격하게 할 수 있진 않다.

다음과 같은 예시가 있기 때문이다.

```java
put((Class)Integer.class, "문자열~");
Integer value = get(Integer.class) // ClassCastException 발생
```

그래서 `put`과 `get` 메소드에 `Class<T>`에 있는 `cast`메소드를 사용한다.

`cast`메소드는 사실 별거없다. 그냥 인스턴스 검사를 해줌으로서 좀 더 안전하게 다운캐스팅해주는 것이다.

이로인해 `TypesafeMap`은 다양한 타입을 커버할 수 있고 타입별로 원소의 타입이 주어진 타입토큰과 일치함이 증명된다.

### 타입 안전 이종 컨테이너 패턴의 구현: `TypeReference<T>`

예제코드 참조할 것

### 타입 안전 이종 컨테이너 패턴의 활용: `Collections.checkedXXX()`

위 패턴을 구현해서 엄격하게 타입을 검사하는 `Collections.checkedXXX()` 가 있다.

기존의 컬렉션에 타입 안전 이종 컨테이너 패턴을 활용한 컬렉션으로 바꾸어 준다.

위의 `TypesafeMap`같은 것을 구현한 것이기 때문에 다음과 같은 시도를 차단할 수 있다.

```java
Map<String, Integer> map = new HashMap<>(); // Java7에 추가된 다이아몬드

// 위의 맵은 사실 map을 Raw타입으로 강제 캐스팅하면 아무런 타입이나 다 넣을 수 있다.
Map unsafeMap = (Map) map;
unsafeMap.put(12, "엄");
// Integer result = map.get(12);
// ClassCastException이 발생한다.

Map<String, Integer> safeMap = Collections.checkedMap(map, String.class, Integer.class);
Map map2 = (Map) safeMap;
// map2.put(12, "엄"); 바로 막히게된다.

map2.get(12);
```

### 한정적 타입 토큰을 활용해보자

한정적 타입 토큰이란? 타입토큰에 한정자가 들어가는것으로 

현재의 제네릭 메서드는 `<T>`라는 타입 매개변수를 가지지만, 이 타입 선언부에 한정자를 추가하여 타입 제한을 걸 수 있다.

그런데 비 한정적 와일드카드 타입으로 된 타입토큰을 한정적 타입토큰의 인자로 넘기려면 어떻게 해야할까?

명시적인 타입캐스팅은 역시나 `unchecked`경고를 만들어낼 것이다.

다행히 `Class`클래스에는 이런상황에 대하여 안전하게 다운캐스팅 변환이가능하도록 `asSubclass()`라는 메소드가 존재한다.

위 메소드를 활용하면 인스턴스 검사 후 안전하게 다운캐스팅할 수 있다.

다음은 해당하는 예시로서 한정적 타입토큰 인자에 비 한정적 와일드카드 타입의 타입토큰을 대입할때 `asSubclass`를 활용하는 예제이다.

`AnnotatedElement.getAnntoation()`은 한정적 타입토큰을 사용하고 있고, 런타임에 동적으로 들고오는 어노테이션의 클래스 메타정보의 타입은 `Class<?>`이다.

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Anno {
}
```
```java
public class AnnotationExtractor {
    static Annotation getAnnotation(AnnotatedElement element, String annotationTypedName){
        Class<?> annotationType = null;
        try {
            annotationType = Class.forName(annotationTypedName);
        } catch (Exception e){
            throw new IllegalArgumentException(e);
        }

        return element.getAnnotation(
                annotationType.asSubclass(Annotation.class)
                // 한정적 타입에 asSubclass를 통해 다운캐스팅 될 수 있는지 검사하고 
                // 캐스팅 가능하다면 대입할 수 있게 캐스팅하여 대입한다.
        );
    }

    @Anno
    public int method(){
        return 0;
    }
    public static void main(String[] args) throws Exception {

        AnnotationExtractor extractor = new AnnotationExtractor();

        AnnotatedElement element = extractor.getClass().getMethod("method");
        Annotation annotation = getAnnotation(element, "com.khc.practice.effectivejava.ch05.item33.Anno");
        System.out.println(annotation.annotationType().getName());
    }
}
```



