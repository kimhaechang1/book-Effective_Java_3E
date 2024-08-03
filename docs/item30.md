<a href="../src/item30">예제 코드</a>

### 제네릭 메서드를 활용한 Case #1: 내가 만드는 합집합 메소드 union

예를들어 두 집합의 합집합을 구하는 메소드인 `union` 메소드를 구현한다고 했을 때

제네릭이 있기 전까지는 다음과 같았을 것이다.

```java
public static Set nonGenericUnion(Set s1, Set s2){
    Set result = new HashSet(s1);
    result.addAll(s2);
    return result;
}
```
하지만 위의 메서드는 `Raw 타입`을 사용하였기에, 만약 메소드 내부에서 두 집합 어디든 상관없이 변조를 가하게 되면

이전 사용자의 의도와는 다른 타입의 원소가 Set 내부로 들어가게 된다.

그래서 타입 안정성을 늘리기 위해 제네릭 메소드를 사용한다.

```java
public static <E> Set<E> genericUnion(Set<E> s1, Set<E> s2){
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}
```
하지만 여전히 아쉬운게 있다면, 현재 타입 파라미터 `E`로 인해 모든 타입이 같아야 한다는 점이다.

이러한 타입 제한으로부터 조금 자유로워 질 수 있도록 `한정적 와일드 타입`(<a href="docs/item31.md">아이템31</a>)을 사용하여 유연하게 개선할 수 있다.

```java
public static <E> Set<E> wildcardUnion(Set<? extends E> s1, Set<? extends E> s2){
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}

public static void main(String[] args) {
    Set<Double> set1 = Set.of(10.5,125.5,12.4);;
    Set<Integer> set2 = Set.of(1,5,6,7);
    Set<Number> result = wildcardUnion(set1, set2);

    for(Number value: result)
        System.out.println("value: "+value);
}
```

### 제네릭 메서드를 활용한 Case #2: 제네릭 싱글턴 팩토리

때로는 불변 객체를 여러 타입으로 활용할 수 있게 만들어야 할 때가 있다.

이럴때 런타임에 타입이 소거되는 원리를 사용하여 어떤 타입으로든 매개변수화 할 수 있다.

이러한 요청되어지는 타입에 따라 캐스팅하여 객체를 리턴하는 것을 `제네릭 싱글턴 팩토리`라고 한다.

아래는 `제네릭 싱글턴 팩토리`의 예시코드이다.

```java

class Moneys{

    // 유용한 메소드들만 넣어놓은 클래스
    private Moneys(){

    }
    private static final List<Money> collection = new ArrayList<>();

    public static <T extends Money> List<T> getSingletonGenericMoneyFactory(){
        // 타입소거에 의해 실체화 불가 타입들은 모두 제거된다.
        // 따라서 기존에 List<Money> 타입은 List로 제거되고, List -> List로의 캐스팅 이므로 문제없이 이뤄진다.
        // 다만 Raw 타입끼리의 캐스팅이기 때문에 명시적인 타입을 컴파일러가 보장해줄 수 없다.
        return (List<T>) collection;
    }
}

class Money{
    int value;

    public Money(int value){
        this.value = value;
    }
}

class Dolar extends Money{

    public Dolar(int value) {
        super(value);
    }

    public String toString(){
        return "[ value: "+this.value +" $ ]";
    }
}

class Won extends Money{


    public Won(int value) {
        super(value);
    }
    public String toString(){
        return "[ value: "+this.value +" 원 ]";
    }
}

public class Practice {

    public static void main(String[] args) {
        List<Dolar> list1 = List.of(new Dolar(10), new Dolar(15));
        List<Won> list2 = List.of(new Won(10), new Won(15));
        List<Dolar> dolarList = Moneys.getSingletonGenericMoneyFactory();
        List<Won> wonList = Moneys.getSingletonGenericMoneyFactory();
        dolarList.addAll(list1);
        wonList.addAll(list2);

        System.out.println(list1);
        System.out.println(list2);
    }
}
```

### 제네릭 메서드를 활용한 Case #3: 재귀적 타입 한정

일반적으로 자기자신과 같은 타입끼리 비교를 많이 한다.

그리고 그 비교에 사용되는 인터페이스로 `Comparable<T>`를 사용한다.

```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```
이때 T는 자기자신과 비교가능한 원소들을 의미하며 즉, 비교가능한 클래스임을 명시한다.

이를 타입 한정자 `extends`와 함께 사용하면 비교가능한 모든 클래스에 대한 유틸리티 메소드를 만들 수 있다.

한정자를 사용해서 `Moneys`에 최대금액을 찾는 메소드를 만들어보자.

`Money`에 `Comparable`을 구현하여 비교기준을 정하자.

```java
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

    public Money(int value){
        this.value = value;
    }

    @Override
    public int compareTo(Money o) {
        return this.value - o.value;
    }
}
```

그런데 이렇게 구현하면 융통성이 없는 `max`메소드가 만들어진다. 

왜냐하면 현재는 모든원소 E에 대하여 비교가능한것은 `Comparable<E>`이기 때문에 자기자신한테 `Comparable`이 있는 개체여야 동작 가능하다.

결과적으로 재귀적 한정 타입은 마치 상위클래스의 메소드를 사용하는데 있어서, 타입정보를 넘기는 행위처럼 보인다.

그리고 재귀적 한정 타입을 사용하면 상위타입에서 상속받는 클래스에 대해서 입력 매게변수에 대한 제한을 걸수 있다.

즉, 아무거나 넣지 못하게 하고 같은 카테고리끼리 묶는데 문법적으로 강제할 수 있다는 것

