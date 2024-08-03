### 한정자를 사용한 유연성 극대화: PECS

일반적인 `Stack`이 있다고 가정해보자.

```java
public class Stack<E> {

    public Stack() {}
    public void push(E e){}
    public E pop(){
        return null;
    }
    public boolean isEmpty(){
        return false;
    }

    public void pushAll(Iterator<E> src){        

    }
}
```
내부 구현이 비어있더라도 신경안써도 된다.

여기서 `Stack<Number>`로 선언하고 `Iterator<Integer>`를 사용해서 `pushAll` 메소드를 작동시키려 한다.

될것 같지만 `E` 를따라가기에 `Iterator<Number>`가 되어버리고, 제네릭은 공변성을 띄지 않기 때문에 컴파일 타입검사에서 컴파일 오류가 발생한다.

위와같은 상황에서 유연성을 극대화 하기 위해서는 **원소의 생상자나 소비자용 매개변수에 와일드 카드 타입을 사용**하는게 좋다.

그에 대한 공식으로 `PECS(Producer-extends, consumer-super)`가 있다.

말 뜻 그대로 타입 파라미터(`T`)로 매게변수화(`List<T>`) 타입의 값을 생산하기만 한다면 `extends` 한정자를 고려하고, 파라미터 타입으로 값을 소비하기만 하면 `super`를 고려하면 된다.

위와 동일한 예시에서 모든 원소를 인자로 넘어온 `Collection<E>` 에 넣는 `popAll` 메소드를 추가하려 한다.

이는 결국 매개변수화 타입(`Stack<E>`)에서 타입 파라미터 `E` 타입 원소를 소비하는 것으로서 

```java
public void popAll(Collection<? super E> dst){

}
```
로 설계하면 된다.

이렇게 유연성을 늘리는데에 도움이 되지만, 중요한 점은 **클라이언트가 와일드카드 타입을 신경쓰지 않아야 한다**는 점이다.

### PECS의 Complex example: `<E extends Comparable<? super E>> E method( Collection<? extends E> )`

기존의 아이템 31의 예제인 <a href="../src/item30/recursivetypebound/Practice.java">Moneys 클래스</a>가 유연하지 못한 `max`메서드를 갖고 있던것이 있다.

여기서 유연하지 못했던 이유는 자기자신과 비교하기 때문이다.

우선 기존에 인자는 단순히 `Collection<E>` 였지만, PECS로 해석하면 이제 `E`타입 인스턴스를 제공하는 입장이기에 `? extends E`가 될 수 있다.

이와 동시에 기존에 `Comparable<E>`를 해석하자면 `E`타입 인스턴스를 사용하여 정수값을 만들어낸다. 

그래서 `<E extends Comparable<E>>`는 `<E extends Comparable<? super E>>` 로 해석될 수 있다.

이에 따라 다음과 같이 이제 `Dolar`클래스도 `Won`클래스도 구분없이 `Money` 한곳에 정의된 `Comparable`을 따라가면 된다.

```java
public static <E extends Comparable<? super E>> E max(Collection<? extends E> c){
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
```

이제 `max` 메소드는 아주 유연해졌다.

```java
public class Practice {

    public static void main(String[] args) {
        List<Dolar> dolarList = List.of(new Dolar(15),new Dolar(1), new Dolar(55));
        Dolar max = Moneys.max(dolarList);
    }
}
```

### 타입 매개변수도 괜찮고 비 한정적 와일드 카드를 사용해도 괜찮다면?

둘 사용이 모두 자유롭고 타입 매개변수가 한번만 나온다면 비 한정적 와일드 카드로 대체하는것이 좋다.

클라이언트 입장에서 더더욱 타입을 신경쓰지 않아도 된다. 하지만 비 한정적 와일드카드로 타입 파라미터가 설정된 파라미터의 경우

해당 컬렉션에 대해 변조를 가할 숴 없다는점이 있다. 이를 해결 하기 위해서 타입이 하나로 결정된 `Helper` 메소드를 사용한다.

관련 예제는 <a href="../docs/java_capture_of.md">제네릭 캡처</a>를 한번 더 살펴보자.