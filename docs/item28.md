## 배열은 공변적이며 실체화된 타입이다.

배열이 `공변적`이란 얘기는 어떤 `Sub`타입이 `Super`타입의 하위타입일때 `Sub[]` 는 `Super[]` 가 가능하단 이야기이다.

실체화된 타입이라는 말은 런타임에도 자신이 담기로한 원소의 타입을 인지하고 확인한다.

이것과 비교해서 제네릭은 런타임때는 이미 타입이 규칙에 의거하여 `소거`되어있다.

```java
public class Practice {

    public static void main(String[] args) {
        Object[] objectArr = new Long[2];
        // 배열은 공변적이기 때문에 가능하다.

        objectArr[0] = "김회창";
        // ArrayStoreException이라는 RuntimeException이 발생한다.
    }
}
```

이러한 차이로 인해 배열과 제네릭은 주요 비교대상이며, 잘 어울리지 못한다.

자연스럽게 제네릭타입, 파라미터화 타입, 타입 매개변수로서 배열을 사용할 수 없다.

```java
new List<E>[] // 제네릭 타입을 받아서 사용하는 배열은 만들 수 없다.

new List<String>[] // 파라미터화 타입

new E[] // 타입 파라미터
```

만약 위의 제네릭들에 대해서 배열과 어울러지는것을 허용한다고 가정해보자.

그상황에서 다음의 코드가 실행이 되어버리는 큰 문제가 발생한다.

```java
List<String>[] stringLists = new List<String>[1];   // 원래는 파라미터화 타입에는 배열을 쓸수없지만 허용되었다고 가정
List<Integer> intList = List.of(42);                // Integer 값 하나를 담고있는 List<Integer> 객체하나 초기화
Object[] objects = stringLists;                     // Object[] 배열로의 공변 가능하다.
objects[0] = intList;                               // 마치 Object object = List<Integer>를 넣는것에서 List에는 제네릭이 소거되므로 문제없다.
String s = stringLists[0].get(0)                    // get(0)는 Integer 값이고, 컴파일러가 String으로 캐스팅필요성을 느껴 캐스팅함으로서 ClassCastException이 발생한다.
```
애초에 이걸 막으려면 맨 윗줄의 대입부터 막아야한다.

배열을 제네릭으로 만드는것이 귀찮을 수 있다.

- 제네릭 컬렉션에서는 자신의 원소 타입을 담은 배열을 반환하는 게 보통은 불가능하다. (완벽하지는 않지만, <a href="docs/item33.md">아이템33</a> 에서 일부 문제를 해결해주는 방법을 제시한다.)

- 제네릭 타입과 가변인수 메서드를 함께 쓰면 해석하기 어려운 경고 메시지를 받게 된다. 이는 가변인수 메서드를 호출할 때 마다 매개변수를 담을 배열이 하나 만들어지는데, 이때 원소가 `실체화 불가 타입`이라면 경고가 발생한다.

### 실체화 타입과 비 실체화 타입

`실체화 타입`이란 런타임 시간동안에도 그 타입에 대한 정보가 완전히 유지되는 것을 의미한다. 

- 원시타입, 제네릭이 아닌 타입, Raw 타입, 비 한정적 와일드 카드

`비 실체화 타입`이란 컴파일 시간에 타입소거에 의해 제거되는 타입을 의미한다. 

- 와일드카드를 포함하고 있지 않은 제네릭 타입

### 배열을 제네릭으로 변환하여 비검사 경고를 막는 Case

<a href="../src/item28">예제 코드</a>

다음의 코드는 컬렉션안의 원소들 중 하나를 랜덤하게 뽑는 메소드 `choose`를 제공하는 `Chooser` 클래스이다.

```java
public class Chooser {

    private final Object[] choiceArray;

    public Chooser(Collection choices) {
        choiceArray = choices.toArray();
    }
    
    public Object choose(){
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
}
```

위의 코드에서 생성자로 들어오는 `Collection`이 `Raw`타입이기 때문에 타입 안정성이 보장되지 못한다.

그래서 `choose` 메소드를 호출한 결과를 함부러 캐스팅 했다가 `ClassCastException`이 발생할 수 있다.

```java
public class Chooser<T> {

    private final T[] choiceArray;

    public Chooser(Collection<T> choices) {
        // choiceArray = choices.toArray(); // 여기를 형변환 해주어야 한다.
        choiceArray = (T[]) choices.toArray();
        // toArray()의 결과는 Object[] 이나, T에는 무슨 타입이 올지 모르므로 경고를 띄우게 된다.
    }

    public Object choose(){
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
}
```

위의 코드에서 부터는 사실 실제동작에 문제가없다.

하지만 제네릭의 타입은 컴파일에 문제가 없다면 전부 소거되고, `T[]`로의 변경이 안전한지 검사할 수 없기 때문에 경고를 발생시킨다.

이 경고를 억누르는 방법도 있지만, 캐스팅 문제에 원인이 되는 배열을 리스트로 바꾸는것이 답일수도 있다.

```java
public class Chooser<T> {

    private final List<T> choiceList;

    public Chooser(Collection<T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public Object choose(){
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }
}
```

