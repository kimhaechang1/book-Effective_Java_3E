## 제네릭과 가변인수

`가변인수(varargs)`는 말뜻 그대로 개수가 여러개 올 수 있는 매개변수를 가지는 것을 의미한다.

이들 가변인수는 동작할때 내부적으로 담을 배열을 만들고 해당 배열로 가변인수를 다룬다.

이러한 가변인수에는 제네릭이나 타입 파라미터가 가능하다. (`List<String> ...stringLists`, `T ...t`)

그런데 코드상에서는 직접 구현하면 안되는 위의 사항들이 가변인수에서는 허용된다.

그 이유는 실무에서 매우 유용하기 때문에 모순을 어느정도 인정하는 것이다.

```java
Arrays.asList(T... a) 등
```

Java에서는 이 구현방식이 위험하게도 클라이언트에게 노출되게 된다. 

그래서 제네릭이나 매게변수화된 타입과 함께 사용할 때에는 **파라미터화된 타입이 그렇지 않은 타입을 참조할때 발생하는** `힙 오염`이 발생한다.

힙 오염은 그래서 주로 `Raw`타입과 파라미터화 타입을 섞어 사용하면 발생한다.

그렇기에 다음과 같은 규칙을 준수하여야 한다.

- varargs 매개변수 배열에 아무것도 저장하지 않는다.

- 배열을 신뢰할 수 없는 코드에 노출하지 않는다.

### varargs 매개변수 변조를 가한 경우: Heap pollution

아래의 코드는 컴파일타임과 런타임에 파라미터화 타입이 달라져서 힙 오염이 발생하는 예시이다.

```java
static void dangerous(List<String> ... stringLists){
    List<Integer> intList = List.of(42);
    Object[] objects = stringLists;
    objects[0] = intList;
    String s = stringLists[0].get(0);
}
```

위 코드가 컴파일되고 나면 비 실체화 타입은 모두 타입소거가 된다.

```java
static void dangerous(List ...stringLists){
    List intList = List.of(42);
    Object[] objects = stringLists;
    objects[0] = intList;
    String s = stringLists[0].get(0); 
    // 컴파일러가 타입추론을 통해 오른쪽의 Object타입을 String에 대입하기 위해 캐스팅을 추가한다.
    // 하지만 stringLists[0].get(0)의 타입은 Integer이기 때문에 ClassCastException이 발생한다.
}
```

그리고 배열은 공변성이 있고 `Object`는 `List`의 상위타입이므로 `Object[]` 에 `List[]`가 대입되는것은 문제가 없어진다.

### 안전하지 않는 메소드에 varargs 매개변수 노출

우선 아래의 메소드는 `varargs` 매개변수를 그대로 노출시킨다.

배열은 가변인수기 때문에 무조건 생성된다.

```java
static <T> T[] toArray(T ...args){
    return args;
}
```

`toArray`를 직접 사용하는것은 큰 문제가 되지않으나...

만약 다음의 메소드를 거쳐서 사용한다면

```java
static <T> T[] pickTwo(T a, T b, T c){
    switch(ThreadLocalRandom.current().nextInt(3)){
        case 0: return toArray(a, b);
        case 1: return toArray(a, c);
        case 2: return toArray(b, c);
    }
    throw new AssertionError();
}

public static void main(String[] args) {
    String s1 = "좋은";
    String s2 = "빠른";
    String s3 = "저렴한";
    String[] results = pickTwo(s1, s2, s3);
}
```
`T[]`가 항상 `Object` 배열을 리턴하고, 또 제네릭 타입이기에 `String[]`과 맞추기 위해 컴파일러가 `String[]`타입으로 캐스팅한다.

그 결과 `Object`배열은 `String`배열의 하위타입이 아니기에 `ClassCastException`이 발생한다.

자세하게 알아보자면 만약 두 메서드에서 `toArray`를 바로사용하는것은 문제가 되지 않는데, 이유는 `T`타입이 컴파일러가 뭔지 추론하여 `String` 배열을 준비하기 때문이다.

그래서 결과적으로 `toArray`가 가변인수 배열로 생성하는 `Object[]`에 `String[]`을 대입하여 작업하고 `Object[]`를 반환하더라도, 그 인스턴스인 `String[]`이 유지되므로 `String[]`으로의 캐스팅이 문제가 되지 않는다.

하지만 `pickTwo`메소드 에서 `toArray()`를 호출하는경우는 `pickTwo`입장에서는 추론상 `String`인걸 인지하고 있다. 

이걸 뭐 별다른 캐스팅이 필요하지 않기에, 타입소거에 의해 `Object[] pickTwo(Object a, Object b, Object c)`로 인지하게 된다.

즉, a와 b와 c는 각각 `Object object = new String()`인 셈으로 대입되어 있다.

하지만 이들의 인스턴스가 `String`일 뿐이지 타입은 `Object`이다. 따라서 `toArray()`메소드 입장에서는 `Object[]`배열을 준비하게 되고

내부적으로는 `Object[] objects = new Object[]{a, b}`로서 사용하게 될 것이다.

그러면 `objects` 내부의 원소 a, b는 `String` 타입이겠지만, `Objects` 자체는 구체적인 타입으로 `Object[]`이기에 `String[]`으로 캐스팅 할 수없다.

물론 이거는 어디까지나 가변인수 배열의 생성방식이 문제가 된다. 어떤것이 문제냐? 타입을 추론하고 수용할 수 있는 타입에 대해서 배열을 새롭게 만들어내는것이다.

그래서 아래의 코드는 실행된다.

```java
static <T> T get(T t){
    return another(t);
}

static <T> T another(T t){
    return t;
}

public static void main(String[] args) {
    String str = get("김회창");
    // 차근차근 생각해보자
    // 우선 소거가 되면서 위의 메소드들이야 뭐 T가 전부 Object가 될것이다.
    // 그러고 String이 넣은상황은 사실상 
    // Object t = new String()
    // Object anotherT = t; 
    // 로 해석이 되고
    // 끝까지 인스턴스는 new String()으로 변화가 없다.
    // 따라서 str로의 대입에 대해서 추론상 (String) 캐스팅을 해주고 실제 런타임에서도 문제가 없다.
}
```

그럼 무조건적으로 가변인수를 다른 메소드에 사용하면 안되는걸까?

두가지 예외사항이 있다.

- 타겟 메소드가 `@SafeVarargs` 어노테이션이 달려있는 경우

- 배열 내용의 일부만으로 함수를 호출만 하는 경우

### @SafeVarargs말고 컬렉션을 사용하는 것도 방법이다.

말그대로 위의 예제에서 제네릭 가변인수를 사용하여 `HeapPollution`을 걱정할빠엔

조금 더 메모리가 발생하고 속도가 느리고 클라이언트 입장에서 코드가 더러워지더라도

`List`로 대체하는것도 하나의 방법이다.

```java
static <T> List<T> pickTwoList(T a, T b, T c){
    switch(ThreadLocalRandom.current().nextInt(3)){
        case 0: return List.of(a, b);
        case 1: return List.of(a, c);
        case 2: return List.of(b, c);
    }
    throw new AssertionError();
}

public static void main(String[] args) {

    String s1 = "좋은";
    String s2 = "빠른";
    String s3 = "저렴한";

    List<String> result = pickTwoList(s1, s2, s3);
    // List 에 List를 대입하므로 아무런 문제가 없다.
    System.out.println(result);
}
```







