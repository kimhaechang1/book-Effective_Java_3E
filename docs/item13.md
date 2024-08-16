## clone 재정의는 주의해서 진행하라

일단 `clone` 메소드는 일반적으로 `Object`에 있다고 생각할 수 있다.

`Object`에 보면 `protected` 접근제한자를 가지고 있다. 따라서 사용할꺼라면 하위 클래스에서 오버라이딩 해서 사용해야 한다.

근데 막상 오버라이딩해서 `super`의 `clone`을 호출하면 `CloneNotSupportException`이 발생한다.

```java
class Animal {
    String type;

    public Animal(String type) {
        this.type = type;
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        
        return super.clone();
    }
}
```
```java
public static void method1() {
    Animal animal = new Animal("Cat");

    try {
        // CloneNotSupportedException이 발생하는 부분
        Animal clonedAnimal = (Animal) animal.clone();
        System.out.println("Cloned Animal: " + clonedAnimal.type);
    } catch (CloneNotSupportedException e) {
        // 예외가 발생했을 때 처리
        System.out.println("CloneNotSupportedException 발생: 객체가 복제 불가능합니다.");
        e.printStackTrace();
    }
}
```
이제 뭐가 빠졌나? `super.clone`인 즉 `Object`의 `clone`메소드를 사용하려면 `Cloneable`이란 마커 인터페이스를 반드시 구현해야 한다.

그래서 위의 상황에서 `Animal`클래스에 `Cloneable`를 호출하면 문제가 발생하지 않는다.

그래서 아래의 주의사항들을 거쳐서 `clone`을 재정의하면 문제가 없다.

### 주의사항: 연쇄적인 clone 재정의

A클래스와 A클래스를 상속받는 B클래스가 있다고 하자.

사실 clone은 복사된 새로운 객체를 만드는것이기 때문에 clone 메소드가 `new A()`를 반환하더라도 큰 문제가 되지않는다.

하지만 이는 A를 final 클래스로 두었을 경우에만 합당하고, 확장에 있어서 열려있는 경우 (B와 같이) 위와 같은 재정의는 문제를 발생시킨다.

왜냐하면 A를 확장한 B클래스를 설계하는 사람은 clone을 구현할때 A도 연쇄적으로 상위 clone을 호출하여 최상위 `Object`의 `clone`을 호출하기를 기대하기 때문이다.

```java
class A implements Cloneable{

    // 컴파일러는 승인하지만 원하는데로 동작하지 않는다.
    public Object clone () throws CloneNotSupportedException {
        return new A();
    }
}

class B extends A{

    public B clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

public class Practice {
    public static void method2() throws CloneNotSupportedException {
        B b = new B();
        System.out.println(b.clone() instanceof B); // false
        B bClone = (B) b.clone(); // cannot cast A to B Exception 발생
        System.out.println(b);
        System.out.println(bClone);
        System.out.println(bClone.equals(b));
    }

    public static void main(String[] args) {
        try {
            method2();
        }  catch (CloneNotSupportedException e) {
            System.out.println("CloneNotSupportedException 발생: 객체가 복제 불가능합니다.");
            e.printStackTrace();
        }
    }
}
```
그래서 `재정의 하려거든 확장에 닫혀있는 final 클래스이거나, 아니면 연쇄 호출을 잘 재정의 하자`.

참고로 재정의 할때 위의 `B bClone = (B) b.clone` 과 같이 클라이언트에게 형변환을 시키지 않는것이 좋다.

왜냐하면 쓸데없이 `CloneNotSupportedException`이 `checked exception`이라서, 내부적으로 clone 재정의에서 형변환 하더라도 아무런 문제가 없는걸 알 기에

그냥 `clone` 메소드 내에서 `try catch` 하는것이 좋다.

```java
class A implements Cloneable{

    // checked exception try catch
    public A clone() {
        try {
            return (A) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }

    }
}

class B extends A{

    public B clone(){
        return (B) super.clone();
    }
}
```

### 주의사항: 가변객체 참조

일반적으로 기본타입 필드이거나 불변 객체 필드의 경우에는 큰 문제가 되지 않지만

`가변타입 객체의 경우 clone할 때 해당 필드도 같이 clone`하여야 

두 복사된 객체 속 필드에서 공통의 가변 객체 참조를 가지지 않는다.

어찌보면 당연하게도, 인스턴스 자체를 heap메모리에서 복사하는거라면 필드 참조객체에 대해 복사를 명령하진 않는것이다.

그래서 필드 참조객체를 clone 메소드에서 같이 해주어야 문제가 발생하지 않는다.

그나마 다행인 점은 `배열에 대한 clone`은 원본 배열 객체의 타입을 그대로 복사해주기 때문에 문제가 없다.

그래서 아래의 Stack예시는 참조배열로서 큰 문제가 없지만

LinkedList 배열을 사용하는 Map의 버킷 객체는 LinkedList 배열만 복사해서 끝나지않고, 그 내부 엔트리까지 전부 복사해야 clone을 완료할 수 있다.

즉, `참조 객체 필드에 대해서 공유되어도 상관이 없지 않은이상 clone된 객체와 원본 객체사이에는 독립적인 객체임이 유지되는것이 좋다.`

```java
public Stack clone() {
    try {
        Stack result = (Stack) super.clone();
        result.elements = elements.clone(); // 가변 객체 elements 복사 명시
        // 만약 final의 경우에는 final을 떼주어야 함
        return result;
    } catch (CloneNotSupportedException e) {
        throw new AssertionError();
    }
}
```

```java
public class HashTable implements Cloneable {


    private Entry[] buckets;

    private static final INIT_SIZE = 26;

    private static class Entry {
        final Object key;
        Object value;
        Entry next;



        Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    public HashTable() {
        buckets = new Entry[INIT_SIZE];
    }

    @Override public HashTable clone() {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = new Entry[buckets.length];
            for(int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null) {
                    result.buckets[i] = buckets[i].deepCopy();
                }
            }
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    Entry deepCopy() {
        // 연결리스트 내부 복사
        Entry result = new Entry(key, value, next);
        // 초기값 엔트리 하나 넣어놓고

        for(Entry p = result; p.next != null ; p = p.next) 
            // 다음 다음 돌면서 복사
            p.next = new Entry(p.next.key, p.next.value, p.next.next);
        return result;
    }
}
```

### 주의사항: 오버라이딩 메소드를 clone에서 사용하지 말 것

말그래도 재정의 가능한 메소드를 clone에서 사용하말라는 것이며, 특히나 해당 값에 의존하게 되면 복제본과 원본값에 차이가 발생할 수 있다.

<a href="../src/item12/calloverridemethod/">예시</a>

### 대안책: 복사 팩터리와 복사 생성자를 사용하자

자기자신과 같은 클래스의 인스턴스를 받아서 복사해주는 메소드로서

이전의 clone을 사용할 때의 규약을 깔끔하게 처리할 수 있다.

```java
public Data(Data d) {
    // 복사 생성자
}

public Data(Data클래스를 윗도는 인터페이스로 받아도 됨) {
    // 복사 생성자
}

public static Data newInstance(Data d) {
    // 복사 팩터리 메소드
}
```

예시로는 HashSet 타입의 변수 s는 크게 clone 메소드를 생각할거 없이 TreeSet<>(s)로서 TreeSet으로 변환이 가능하다.




