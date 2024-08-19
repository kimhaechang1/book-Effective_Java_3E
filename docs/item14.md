## Comparable을 구현할지 고려하라

Comparable 인터페이스는 일반적으로 equals 규약과 비슷하다.

equals규약과 비슷하기에 대칭성, 추이성을 만족하고, 특히나 새로운 확장 클래스에서 추가된 필드에 대한 추이성을 지킬 방법이 없다는것 까지 동일하다.

그 중에서도 필수는 아니지만 지켜야 하는것이 좋다는 것으로 "(x.compareTo(y) == 0) == (x.equals(y))여야 한다." 가 있다.

만약 해당 권고를 지키지 않았다면, 필수가 아니기에 "주의: 이 클래스의 순서는 equals 메소드와 일관되지 않는다." 라는것을 명시해야 한다.

위 규약은 특히나 equals()를 비교 기준 메소드로 잡는 클래스와 compareTo를 비교기준 메소드로 잡는 클래스에서 서로 다른결과를 낳을 수 있다.

그 예시로서 BigDecimal의 객체를 만들때 하나는 1.0 과 다른하나는 1.00 으로 만들고 HashSet에 넣는다면 equals() 메소드를 기준으로 따라가기에 다르다고 나온다.

하지만 둘을 TreeSet에 넣는다면 Comparator의 compareTo()를 사용하기에 같은 값으로 취급되어 TreeSet의 사이즈가 1로 잡힌다.

### 정수타입 비교 연산자와 compare 메소드

기존에는 정수기본타입에 대해서는 '<', '>'를 사용하고, 실수 기본타입 필드를 비교할 때에는 정적 메소드 Float.compare() 등을 사용하는것을 권장하였으나

자바7 부터는 기본타입 박싱 클래스에도 compare 정적 메소드가 추가되었으므로 사용하면 된다.

### JAVA8에 추가된 Comparator 인터페이스의 compareingInt와 thenComparingInt

정적 메소드로서 추가되었고 전자는 Comparator 인스턴스를 만드는 정적 메소드이고, 오른쪽은 부차 순서를 나타낼때 사용한다.

물론 Int가 붙었기에 기본타입의 박싱을 해결해주기 위한 대응되는 메소드라고 생각하면 된다.

특이한점은 아래의 경우에 타입 위트니스를 주지 않으면 컴파일에러가 발생한다.

```java
static void practiceComparing() {
    Comparator<PhoneNumber> comparator = Comparator.comparingInt((PhoneNumber pn) -> pn.areaCode)
            .thenComparingInt((pn) -> pn.prefix)
            .thenComparingInt((pn) -> pn.lineNum);
}
```

람다식의 타입추론에 대해서 어느정도 알고있다면 `PhoneNumber pn`이 아니라 `pn`이어도 작동했던 람다식들이 익숙할 것이다.

하지만 위의 경우 `PhoneNumber`를 빼게되면 컴파일 에러가 발생한다.

그 이유는 comparingInt()의 인자로 받는 IntToFunction의 제네릭 타입에 와일드카드가 있기 때문이다.

그래서 타입 위트니스를 통해 명확한 타입을 제시해주어야 IntToFunction이 올바로 작동한다.

### compare나 compareTo에서 두 값의 차로 비교하는것은 Comparator.comparing 이나 *Type*.compare 메소드로 대체하자.

왜냐하면 어디까지나 연산이기 때문에 정수 오버플를 일으키거나 부동 소수점 방식에 따른 오류를 발생시킬 수 있기 때문이다.

```java
static void overflowComparator() {
    Comparator<Student> overflowComparator = (d1, d2) -> d1.id - d2.id;
    // 오름차순을 유도하였으나...

    Comparator<Student> overflowSafeComparator = (d1, d2) -> Integer.compare(d1.id, d2.id);
    PriorityQueue<Student> pq = new PriorityQueue<>(comparator);
    pq.add(new Student(Integer.MIN_VALUE, "김회창"));
    pq.add(new Student(Integer.MAX_VALUE, "회창김"));
    System.out.println();
    System.out.println(pq.poll());
    // 여기서 id가 Integer.MAX_VALUE인 Student가 나오게 됨, 이에 따라 뺄셈 중 문제가 발생한것으로 보임
}
```
그래서 compare 메소드나 Comparator.comparingInt() 로 교체하면 문제가 해결된다.
```java
static void overflowComparator() {

    Student student1 = new Student(Integer.MIN_VALUE, "김회창");
    Student student2 = new Student(Integer.MAX_VALUE, "회창김");
    Comparator<Student> overflowSafeComparator2 = Comparator.comparingInt((Student d1) -> d1.id);
    Comparator<Student> overflowSafeComparator = (d1, d2) -> Integer.compare(d1.id, d2.id);
    PriorityQueue<Student> pq = new PriorityQueue<>(overflowSafeComparator2);
    pq.add(student1);
    pq.add(student2);
    System.out.println();
    System.out.println(pq.poll());
}
```









