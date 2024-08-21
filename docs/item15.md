## 클래스와 멤버의 접근을 최소화하라

컴포넌트를 독립적으로 설계하기 위해서는 그 객체가 스스로 메세지를 처리할 능력이 있도록 접근성을 좁혀야 한다.

즉, 해당 클래스내의 접근제한자를 가능한한 좁히란 이야기이다.

클래스에 대해서 걸수 있는 접근제한자는 `public`과 `package-private`인데, 가능한한 외부패키지에서 접근을 해야만 하는일이 없다면 `package-private`으로 두자.

멤버에 대해서는 그 수준에 따라 `private` < `package-private` < `protected` < `public` 순서로 접근이 허용되는 범위가 정해진다.

내용에서 "공개 API를 세심히 설계한 후 그 외의 모든 멤버는 `private`으로 만들자. 같은 패키지내에 접근을 허용하고 싶다면 `package-private`으로 풀자."라는 이야기가 있다.

우선 전자의 내용은 객체지향 세계에서 객체들이 서로 메세지를 주고받는 과정이 있는데, 그러려면 기본적으로 객체사이에 소통이 가능해야한다.

이러한 public API부터 설계를 하고난 뒤, 해당 객체만에 독립적인 상태나 메소드를 사용하여 올바른 응답 메세지를 만들어 나가면된다.

### 클래스내의 상수 목적의 static final 제한자에 접근제한자 public일 경우 기본타입 값이거나 불변객체를 참조하여야 한다

흔히 상수를 기록할 때 `public static final`로 두는데 있어서, 이는 클래스 소속이기 때문에 반드시 선언과 초기화가 같은라인에서 수행되어야 하지만,

해당 초기화 되는 객체가 가변일 경우, 다른 클래스에서 객체의 상태를 오염시킬 수 있다.

특히, `배열`의 경우를 조심해야 하는데, 배열필드에 접근하는 접근자를 제공한다면 배열을 오염시킬 수 있기 때문이다.

물론 재할당은 final이기 때문에 안되지만, 그 원소각각에 대한 변조는 막을 수 없기 때문이다.

해결방법은 두가지 있다.

- 기존의 접근제한자를 private으로 바꾸고, 해당 배열에 대하여 불변 컬렉션을 `public static final` 필드로 만든다.

    ```java
    private stataic final Object[] PRIVATE_VALUES = {};

    public static final List<Object> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));
    ```

- 기존의 접근제한자를 private으로 바꾸고 그 복사본을 반환하는 public 메소드를 추가한다.

    ```java
    private stataic final Object[] PRIVATE_VALUES = {};

    public static final Object[] values() {
        return PRIVATE_VALUES.clone();
    }
    ```



