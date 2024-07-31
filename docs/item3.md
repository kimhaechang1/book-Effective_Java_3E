## 싱글턴을 생성하는 두가지 방법

싱글턴이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 의미한다.

싱글턴의 전형적인 예시로는, 함수와 같은 무상태 객체이거나, 설계상 유일해야하는 시스템 컴포넌트가 있다.

싱글턴은 사용하는 클라이언트를 테스트하기가 어려워질 수 있다.

상위 인터페이스가 존재하는 싱글톤 구현 객체가 아니라면, mock 객체 생성에 어려움이 있기 때문이다.

예를들어 매우 연산이 큰 메소드가 있다고 가정하자. 지금 현재로서는 이 메소드에서 해당 큰 연산과 관계 없는 테스트를 하려한다.

만약 해당 싱글톤 객체가 인터페이스의 구현객체라면 테스트에 필요한 기능을 재정의하여 가볍게 사용하는 mock 객체를 생성할 수 있어서 효율적이다.

하지만 싱글톤인 클래스자체는 기본생성자가 private이므로 상속자체가 불가능하므로 무거운 메소드를 그대로 테스트에 사용해야 한다.

싱글턴을 만드는데에는 두가지 방법이 있다.

### public static final field

인스턴스를 생성할 수 있는 생성자에 대한 호출을 `private` 으로 막고

`private` 생성자의 호출을 단 한번만 하여 필드에 해당 클래스의 인스턴스를 `public static final` 제한자로 접근가능하지만 변경이 불가하도록 만드는 방법이다.

```java
public class Elvis {

    private static boolean flag;
    public static final Elvis INSTANCE = new Elvis();

    // private constructor 는 단 한번 호출된다.
    private Elvis(){
    }

    public void leaveTheBuilding() {}
}
```
```java
public class Practice {

    public static void main(String[] args){
        // Elvis elvis = new Elvis(); // private 생성자이기 때문에 막혀있다.
        Elvis elvis1 = Elvis.INSTANCE;
        Elvis elvis2 = Elvis.INSTANCE;
        System.out.println(elvis1 == elvis2); // 같음이 보장된다
    }
}
```

### 정적 팩터리 메서드

정적 팩터리 메서드를 통해 인스턴스를 만들 수 있음을 <a href="docs/item1">item1</a> 을 통해 확인할 수 있었다.

이를 활용하여 생성자와 단하나의 인스턴스 필드를 private으로 만들고 정적 팩터리 메소드를 통해 제공할 수 있다.

```java
public class Elvis {

    private static final Elvis INSTANCE = new Elvis();

    private Elvis() {}

    public static Elvis getInstance() {
        return INSTANCE;
    }
}
```
```java
public class Practice {

    public static void main(String[] args) throws{

        // Elvis elvis = new Elvis(); // private 생성자이기 때문에 막혀있다.
        Elvis elvis1 = Elvis.getInstance();
        Elvis elvis2 = Elvis.getInstance();

        System.out.println(elvis1 == elvis2); // true
    }
}
```

정적 팩터리 메서드로 만들면 다음과 같은 장점이 있다.

- API를 바꾸지 않고도 싱글턴이 아니게 변경할 수 있다.

- 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있다.-> <a href="docs/item30">item30</a>

    - 만약 `Elvis`가 제네릭 클래스라면 다음과같은 제네릭 싱글턴 팩터리가 가능하다.
    ```java
    public class Elvis<T> {

        private T field;

        private static final Elvis<Object> INSTANCE = new Elvis<>();

        private Elvis() {}


        public void setField(T t) {
            this.field = t;
        }
        public T getField() {
            return this.field;
        }

        public static <E> Elvis<E> getInstance() {
            return (Elvis<E>) INSTANCE;
        }
    }
    ```
    잠시 생각해보면 위의 `Elvis<E>`이 어떻게 가능한가 의문점이 들 수 있다. 왜냐하면 제네릭은 무공변성이니까

    ```java
    List<Object> list = new ArrayList<>();
        
    List<String> list3 = (List<String>) list; // cannot convert to List<String>
    ```

    이유는 `E` 와 같은 타입변수를 사용한 메서드 호출에 대해서는
    컴파일러는 단순히 반환타입이 맞는지만 검사하고, 실제 호출을 통한 동작은 런타임때 발생한다.

    ```
    Elvis<String> elvis = Elvis.getInstance() 에서

    컴파일러는 getInstance가 제네릭 메소드 임을 인지하고 E타입을 추론하는데 있어서 문맥상 String임을 간주하고 반환타입이 Elvis<E> 이기 때문에 Elvis<String> = Elvis<String>으로 타입에 문제없음으로 생각하고 넘긴다.

    하지만 아래와 같은 상황에서는 이미 결정된 두 타입에 대해서 무공변성의 제네릭을 캐스팅 하려 했으므로 문제가 발생한다.

    Elvis<Object> elvis1 = Elvis.getInstance();
    Elvis<String> elvis2 = (Elvis<String>) elvis1; // cannot convert error
    ```

    그런데 런타임에는 제네릭이 모두 소거된다. 따라서 런타임 에러는 발생할 여지가 있지만 컴파일 에러는 발생하지 않는다.

- 정적 팩터리의 메소드 참조를 공급자로서 사용할 수 있다. (파라미터가 없는 순수함수이면서 반환타입만을 가짐)

### 두가지 방식의 문제점

두 방식의 문제점은 첫번째로는 리플렉션 API를 통해 강제로 인스턴스를 새롭게 생성할 수 있다는 점이다.

```java
    Elvis elvis1 = Elvis.INSTANCE;
    Elvis elvis2 = Elvis.INSTANCE;
    System.out.println(elvis1 == elvis2); // 같음이 보장된다.


    Constructor cons = elvis1.getClass().getDeclaredConstructor();
    cons.setAccessible(true); // true 의 경우 자바의 접근제한자를 무시해버린다.

    Elvis elvis3 =  (Elvis) cons.newInstance(); 
    System.out.println("elvis1: "+elvis1);
    System.out.println("elvis2: "+elvis2);
    System.out.println("elvis3: "+elvis3); // 혼자 이상한 참조값을 만든다.
```

여기서 리플렉션을 통한 prviate 생성자 호출을 막으려면 한번 더 생성자가 호출되려 할 때 exception을 띄우면 막을 수 있다.

```java
public class Elvis {

    private static boolean flag;
    public static final Elvis INSTANCE = new Elvis();

    // private constructor 는 단 한번 호출된다.
    private Elvis(){
        // 리플렉션을 막을수는 있다.

        if (!flag) flag = true;
        else throw new RuntimeException("리플렉션 막기!");
    }

    public void leaveTheBuilding() {}
}
```

또다른 문제로는 역직렬화 문제이다.

클래스를 직렬화 할 때에는 `Serializable`인터페이스를 구현하는 구현체 클래스여야 하는데

역 직렬화를 하게되면 싱글톤이 깨지게 된다. 

그 이유는 `readObject` 메서드에서 해당클래스의 새로운 인스턴스를 반환하기 때문이다.

따라서 `readObject`를 재 가공하는 `readResolve`를 오버라이딩하여 싱글턴 인스턴스를 리턴해야 하나의 인스턴스임이 보장된다. <a href="docs/item89.md">item89</a>

https://inpa.tistory.com/entry/JAVA-%E2%98%95-%EC%8B%B1%EA%B8%80%ED%86%A4-%EA%B0%9D%EC%B2%B4-%EA%B9%A8%EB%9C%A8%EB%A6%AC%EB%8A%94-%EB%B0%A9%EB%B2%95-%EC%97%AD%EC%A7%81%EB%A0%AC%ED%99%94-%EB%A6%AC%ED%94%8C%EB%A0%89%EC%85%98#%EC%8B%B1%EA%B8%80%ED%86%A4_%EC%97%AD%EC%A7%81%EB%A0%AC%ED%99%94_%EB%8C%80%EC%9D%91_%EB%B0%A9%EC%95%88


### 이 모든 문제를 해결하는 열거형의 활용

열거형을 사용하면 위의 문제를 깔끔하게 해결해줄 수 있다고 한다.

왜냐하면 모든 열거형은 `Enum`클래스를 상속받으며 `Enum`클래스는 `Serializable`인터페이스를 구현하기 때문에 복잡한 직렬화 상황도 해결해줄 수 있다.

그리고 열거형 내의 열거상수들은 필드가 있다면 필드와 함께 즉시초기화 되는 인스턴스이다.

```java
public enum Elvis {

    INSTANCE
    ;

    public void leaveTheBuilding () {}
}
```
열거 상수를 의미해야 하기 때문에 외부로부터의 인스턴스 생성이 private으로 막혀있다.

```java

// Elvis.class
public enum Elvis {
    INSTANCE;

    private Elvis() {
    }

    public void leaveTheBuilding() {
    }
}
```
물론 그렇다고해서 리플렉션 API로 접근이 가능한것은 아니다.

```java
public class Practice {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<Elvis> elvisConstructor = (Constructor<Elvis>) obj.getClass().getDeclaredConstructor(); 
        // Exception in thread "main" java.lang.NoSuchMethodException
        Elvis obj2 = elvisConstructor.newInstance();
        System.out.println(obj2);
    }
}
```