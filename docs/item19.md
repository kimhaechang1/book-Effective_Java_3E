## 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라.

결론부터 얘기하자면 상속을 하는순간 고려해야할 것들을 얘기하고 있다.

앞선 챕터에서 어떤 재정의 가능한 클래스에서 재정의 가능한 메소드가 자기자신의 재정의 가능한 메소드를 호출하고 있는경우 처럼

재정의가 가능하다는 것은 상위 클래스의 변경가능성을 언제나 고려하고 있어야 함은 변함없다.

이를 만족하기 위해서 자바에서는 `@ImplSpec` 자바독 어노테이션 등으로 어떤 상황이 초래될 수 있는지 문서화 해야한다.

### protected 메소드

공개범위에 대해서 `protected`로 어쩔수없이 공개하는 경우도 있다.

`AbstractList`의 `removeRange`의 경우 메소드 설명을 보면 

하위 클래스의 리스트나 서브리스트의 clear() 메소드에서 호출하고 있으며

이 메소드를 재정의 하면 clear() 메소드의 성능에 영향을 미친다고 되어있다.

```java
이게 무슨말이냐면

ArrayList의 subList 메소드는 사실 ArrayList를 반환하는것이 아닌 AbstractList를 구현한 SubList라는 중첩클래스의 인스턴스가 반환된다.

이것이 무슨 차이를 일으키냐?

ArrayList에서의 clear()는 명백히 재정의 되어있다

하지만 어떤 ArrayList 인스턴스에서 subList() 호출결과로 얻은 인스턴스에 clear()를 호출하면 이는 AbstractList의 clear()를 호출하는것이 된다.

왜냐하면 SubList클래스에는 clear()메소드가 재정의 되어있지 않다.

이는 곧 removeRange()에 영향을 받는다는 것이 된다.

단지 하위 클래스에서 원한다면 재정의 해야하고 그것이 어떤 영향을 미치는지에 대해서 문서화 해야함을 강조하게 된다.
```

즉, 하위 클래스에서 사용이 되어지는 상위 클래스의 멤버의 경우 protected 접근제한자를 둬야할 수도 있다는 것이다.

이를 미리알 수 있는 방법은 없으며, 직접 3개정도의 하위 클래스를 작성하면서 "어 이거 좀 필요하겠는데?"를 느끼는 멤버는 protected를 고려해보고

첫 설계에 protected로 했으나 의외로 하위클래스를 만드는 과정에서 안쓰인다면 private을 고려해보면 된다.

### 상속 가능한 클래스의 생성자에서는 재정의 가능한 메소드를 호출해서는 안된다.

이는 하위 클래스의 생성자는 반드시 상위 클래스의 생성자를 먼저 호출하기 때문에 발생하는 것으로

특히나 해당 재정의 가능한 메소드가 하위 클래스의 필드를 참조할 때 문제가 발생한다.

다음의 예시코드를 보자.

```java
import java.time.Instant;

class Super {

	public Super() {
		// 재정의 가능한 메소드는 잠재적으로 오류를 일으킬 가능성이 높다!!!
		overrideMe();
	}

	public void overrideMe() {
		// overrideMe()는 재정의 가능한 메소드이다.

	}
}

final class Sub extends Super {

	private final Instant instant;

	Sub() {
		// 숨겨져 있지만 컴파일러가 super()를 반드시 호출한다.
		instant = Instant.now();
	}

	@Override
	public void overrideMe() {
		// 재정된 메소드에서 하위 클래스의 멤버에 접근하고 있다.
		System.out.println(instant);
	}
}

public class Practice {

	public static void main(String[] args) {
		Sub sub = new Sub();
		sub.overrideMe();
	}
}

```

Super와 Sub 클래스를 설계하고 사용하는 클라이언트 입장에서는 overrideMe()를 호출하였을 때 2번의 now() 가 호출될줄 알았지만?

사실 첫 overrideMe() 호출때에는 필드가 초기화되지 않았을 때에 호출되었으므로 null 이 출력된다.

그리고 clone() 메소드와 관련한 마크업 어노테이션인 Cloneable과 readObject() 메소드와 관련한 Serializable도 조심해야한다. 

이들 메소드도 사실상 내부적으로 생성자를 호출하는듯이 새로운 인스턴스를 만들기 때문이다. <a href="./item13.md">Item 13</a>

Serializable의 경우 싱글톤이 깨지는 케이스에서 readResolve() 를 써야함을 느꼇을 것이다. <a href="./itme3.md">Item 3</a>

writeReplace()는 재정의 하는경우 해당 메소드의 반환타입이 실제로 직렬화 된다. 

만약 지금 정의하고 있는 클래스가 `확장 가능한 클래스`이며 `Serializable을 구현`하고 있고, `readResolve 혹은 writeResolve를 구현`하고 있다면 

두 메서드의 접근제한자를 private으로 두면 안된다. 

만약 해당 클래스가 직렬화 역직렬화 과정에서 어떤 특별한 과정이 필요한데, 상위 클래스에서 재정의 하고 접근제한자가 private이라서 더이상 재정의가 불가능하다면

하위클래스에서 의도치않은 직렬화 혹은 역직렬화가 될 수 있기 때문이다.

### 재정의 가능 메소드를 냅두면서 안전하게: private 도우미 메소드 사용

기계적인 방법으로 재정의 가능 메소드를 안전하게 바꾸는 방법은 내부동작을 private 도우미 메소드로 옮기고

그 메소드를 호출하도록 바꾸는것이다.

```java
public class WaitQueue {

	private final Queue<Integer> queue = new ArrayDeque<>();

	public void enqueue(Integer element) {
		enqueueHelper(element);
	}

	public void enqueueAll(Collection<Integer> produce) {
		enqueueAllHelper(produce);
	}

	private void enqueueHelper(Integer element) {
		queue.add(element);
	}

	private void enqueueAllHelper(Collection<Integer> produce) {
		produce.forEach(this::enqueueHelper);
	}
}

class WaitCountQueue extends WaitQueue {

	private int count;

	public void enqueue(Integer element) {
		count++;
		super.enqueue(element);
	}

	public void enqueueAll(Collection<Integer> produce) {
		count += produce.size();
		super.enqueueAll(produce);
	}

	public int getCount() {
		return count;
	}

	public static void main(String[] args) {
		WaitCountQueue wcq = new WaitCountQueue();
		wcq.enqueueAll(List.of(1, 2, 3));
		System.out.println(wcq.getCount()); // 기존에 헬퍼 메서드가 없었다면 6이 튀어나옴 -> item18
        // 헬퍼 메서드로 옮겼기에 확장한 부분의 실질적인 상위 클래스가 동작해야할 부분은 독립적으로 빼내게 됨
	}
}
```







