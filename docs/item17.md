## 변경 가능성을 최소화하라

합당한 이유가 없다면, 클래스 내에 필드들은 `private final`이어야 한다.

접근가능성도 최소한으로 줄이고, 가능하자면 불변 상태인것이 좋다.

클래스를 불변으로 만들기 위해서는 다음과 같은 규칙을 따르면 된다.

- 객체의 상태를 변경하는 메서드를 제공하지 않는다.

- 클래스를 확장할 수 없도록 한다.

    - 상속을 막는 방법으로 final 클래스로 선언하는 방법도 있지만, 생성자를 private이나 package-private으로 선언하고, 인스턴스를 정적 팩토리 메소드로 제공하는 방법이 있다.

- 모든 필드를 final로 선언한다.

    - 멀티스레드 환경에서 불변객체의 final필드는 스레드 안전성을 보장한다.

    - <a href="https://stackoverflow.com/questions/22743223/are-final-fields-really-useful-regarding-thread-safety">JMM 과 final field</a>

        - 위에서 말하는 결론은 JVM 구현에 따라 다르지만, 컴파일러가 클래스내에 적어도 final 필드가 하나 있는지 체크하는 부분이 존재한다는것, 그리고 final 필드에 대한 초기화는 곧 volatile 변수에 대한 쓰기와 비슷하다는 것이다.

- 모든 필드를 private으로 선언한다.

    - 물론 public final 로도 충분히 불변임을 보장하지만, 해당 불변 필드에 직접접근하기에 코드를 수정하지 않는이상 표현방식을 바꿀 수 없다.

        - 이게 무슨말이냐? public final 필드를 선공개하고 다른 클라이언트가 사용하고 있다는것은 강하게 의존적이게 된다.

        - 하지만 private final로 감추고 메소드로 공개했을경우, 기존의 메소드(표현방식)을 냅두고, 추가하고싶은만큼 추가하면 그만이다.

- 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다.

    - 이는 final이지만 가변 객체를 필드로 가진 경우를 의미하며, final로서 재 할당이 불가능할 뿐이지, 가변객체의 참조를 얻을순 있다. final 필드가 가변객체의 경우에 접근자 메소드 설계를 방어적복사로 하는것이 좋다.



이러한 불변객체를 만들고나면 얻는 이점들이 있다.

불변 객체라서 생성자에 의한 초기화만 이루어지기에 자연스럽게 Thread-safe 하게 된다.

불변 객체는 동일한 객체에 대해서 clone을 해봤자 똑같은 객체이기 때문에, 복사자체가 의미가 크게 없다.

불변 객체들끼리 자유롭게 공유가 가능하며, 불변 객체끼리는 내부 데이터를 공유할 수 있다.

불변 클래스는 실패 원자성을 제공한다.

- 실패 원자성은 '메소드에서 예외가 발생한 후에도 그 객체는 여전히 호출 전과 똑같은 유효한 상태여야 한다' 라는 성질이다. 어짜피 불변 클래스로 만들어진 불변 객체는 접근자 메소드만을 제공하기 때문에 예외와 상관없이 언제나 일정한 상태를 유지한다.


물론 단점도 있다.

대표적으로 값이 다르다면 반드시 독립된 객체로 만들어야 한다.

예를들어 기존의 객체에서 값을 바꾸는 메소드가 있다고 하자. 불변 객체라면 새로운 불변 객체를 리턴해야 할 것이다.

그렇다는것은 아주 조그마한 변경이 있더라도 그만큼을 새롭게 구축해야 하는것이다.

```java
BigInteger에서 flipBit 메소드는 특정 비트를 교체하기 위해서 새로운 공간과 시간을 잡아먹는다.

public BigInteger flipBit(int n) {
    if (n < 0)
        throw new ArithmeticException("Negative bit address");

    int intNum = n >>> 5;
    int[] result = new int[Math.max(intLength(), intNum+2)];

    for (int i=0; i < result.length; i++)
        result[result.length-i-1] = getInt(i);

    result[result.length-intNum-1] ^= (1 << (n & 31));

    return valueOf(result);
}

하지만 BitSet은 가변객체로서 자기자신의 원하는 비트하나만 상수시간내에 바꿔버린다.

public void flip(int bitIndex) {
    if (bitIndex < 0)
        throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);

    int wordIndex = wordIndex(bitIndex);
    expandTo(wordIndex);

    words[wordIndex] ^= (1L << bitIndex);

    recalculateWordsInUse();
    checkInvariants();
}
```

이러한 부분에 대해서 불변 객체는 내부적으로 가변 동반 클래스를 `package-private`으로 제공하는 경우가 있다.

```
MutableBitInteger 같은 경우가 가변 동반 클래스이다.
```

클라이언트들의 복잡한 연산에 있어서 매순간 위의 `flipBit`처럼 새 할당을 하고있으면 속도가 매우 느리기 때문이다.

물론 클라이언트의 복잡한 연산을 다 예측하여 감출 수 있는 경우를 제외하면 `public`으로 두어야 한다.

```
그 예시가 StringBuilder이다.

String은 기본적으로 불변 클래스이지만, StringBuilder를 통해 불변을 마치 가변처럼 생성할 수 있게 도와준다.
```






