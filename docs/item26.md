## Raw 타입은 사용하지 말라

우선 Raw타입과 제네릭 용어를 이해해보자.

<a href="src/item26">예제 코드</a>

### 제네릭 용어정리

먼저 `제네릭 클래스` 혹은 `제네릭 인터페이스`라는 것은 클래스와 제네릭 `선언부`에 `타입 파라미터`가 쓰인것을 의미한다.

그리고 이 둘을 통틀어 `제네릭 타입`이라고 부르고, 제네릭 타입은 매개변수화 타입을 정의한다.

`Raw타입`은 제네릭 클래스혹은 인터페이스에서 `타입 파라미터를 전혀 사용하지 않은 것`을 의미한다.

```java
public interface List<E> { 
    // List<E> 제네릭 타입 (제네릭 인터페이스), 
    // E 타입 파라미터

}
public static void main(String[] args){

    List list = new ArrayList(); 
    // Raw 타입

    List<String> list = new ArrayList<>(); 
    // 원소 타입이 String인 리스트를 뜻하는 매개변수화 타입
}
```

### Raw타입을 쓰면 안되는 Case 1

일단 제네릭을 사용하는것은 컴파일러에게 타입검사에 있어서 엄격함을 제공하는 것

대부분의 경우에서 왠만하면 런타임 에러를 일으키는것 보다 컴파일 에러를 일으키는것이 좋다.

그리고 검사가 끝나고서 바이트코드화 되었을땐 이미 제네릭 타입은 <a href="docs/java_docs_type_erasure_with_bridge_method.md">타입소거 규칙</a>에 의거하여 제거된다.

이렇게 제거가되는 이유는 제네릭이 등장하기전 즉, `JDK 1.5` 이전의 사양에서도 호환이 있어야 하기 때문에 소거방식을 사용한다.

그러면 사실 `List` 인터페이스를 예시로 들면 `List` 와 `List<Object>`는 같은것 아닐까 라는 의심을 할 수 있다.

엄밀하게 따지면 `Object`타입을 수용하는것이 되지만, 전자는 제네릭이 제공하는 컴파일시간의 엄격한 타입검사를 제공받지 못한다.

```java
public class Practice {

    public static void unSafeAddElement(List list, Object o){
        // Raw타입으로 형변환 
        list.add(o);
    }

    public static void safeAddElement(List<Object> list, Object o){
        // List<Object> 타입으로 형변환
        list.add(o);
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        unSafeAddElement(list, Integer.valueOf(2));
        String s = list.get(0); 
        // Runtime Exception 발생: Integer를 String으로 형변환 하려 했으므로
    }
}
```
저기서 `unSafeAddElement`가 `safeAddElement`로 변경되면 컴파일러에 의한 타입검사로 Object List에 String List를 할당하려 했으므로 컴파일 에러가 발생한다.

### Raw타입을 쓰면 안되는 Case 2

다음 두 집합에서 같은 원소의 개수를 셈하는 메소드를 살펴보자.

```java
public static void unboundedGeneric2(Set set1, Set set2){
    int result = 0;
    set1.add(new String("fdafa")); // 가능하다.
    for(Object o1: set1){
        if(set2.contains(o1)){
            result++;
        }
    }
    System.out.println("중복원소 개수: "+result);
}
public static void main(String[] args) {
    Set<Integer> set1 = new HashSet<>(Set.of(1, 2, 3, 4, 5));
    Set<Integer> set2 = new HashSet<>(Set.of(2, 3, 4));
    unboundedGeneric2(set1, set2);
}
```
위의 파라미터 `set1`과 `set2`는 `Raw`타입 이기 때문에, 해당 파라미터로 넘어온 객체에 변조를 가 할 경우

어떤 타입이던지 추가할 수 있게 되고, 이로인해 읽는 메소드 혹은 클라이언트에서 예기치못한 런타임 에러를 받을 수 있다.

사실 저기서 의도한 것은 **"Set에 원소가 사실 무슨타입이든 관심없음"**을 의도 했을 것이다.

그러면 `비 한정적 와일드 카드 타입`인 `<?>`를 사용하면 된다.

이는 어떠한 타입도 올 수 있지만, 어떠한 타입도 결정난게 없기 때문에 변조를 가할 수 없게 된다.

```java
public static void unboundedGeneric(Set<?> set1, Set<?> set2){
    int result = 0;

    // set1.add(); capture of ? error

    for(Object o1: set1){
        if(set2.contains(o1)){
            result++;
        }
    }
    System.out.println("중복원소 개수: "+result);
}
```
