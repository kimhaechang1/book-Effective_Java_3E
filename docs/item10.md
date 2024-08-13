## equals는 일반 규약을 지켜 재정의 하라

일단 아래의 상황이면 재정의 하지말아야 한다.

- 각 인스턴스가 본질적으로 고유하다.

- 즉, 논리적 동치성을 검사할 일이 없다.

- 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞는다.

- 클래스가 private이거나 package-private이고 equals 메서드를 호출할 일이 없다.

equals를 언제 재정의 해야하는걸까?

기본적으로 equals는 인스턴스 참조값이 같은가를 따지게 된다. (Object equals)

이것이 아니라 논리적으로 같아야 함을 비교하도록 값 클래스인경우엔 재정의 해야한다.

재정의에는 다음을 만족해야 한다.

- 반사성: 객체는 자기자신과 같아야 한다는 뜻, x.equals(x) == true

- 대칭성: 두 null 아닌 객체 x, y에 대하여 x.equals(y) == true이면 y.equals(x) == true여야 한다.

    - 위의 대칭성은 완벽히 대체가능하지않은 객체들을 비교하려 할때 문제가 발생한다.

    - <a href="../src/item10/symmetric/">예제 코드</a>

- 추이성: 세 null 아닌 객체 x, y, z에 대하여 x.equals(y) == y.equals(z) 이면 x.equals(z)도 그 값과 같아야 한다.

- 일관성: 두 객체가 같다면 불변 객체의 경우에는 끝까지 같던가 끝까지 달라야한다.

    - 어찌됬건 불변이던 가변이던 equals 의 판단에 신뢰할 수없는 자원이 끼어들면 안된다 (수시로 바뀌는 값)

- null-아님: instanceof 키워드를 통해 묵시적으로 null 검사가 되어 타입확인전에 체크할 수 있으므로 명시적으로 비용을 들여 null 체크를 하지 않아도 된다.

### 추이성과 리스코프 치환원칙

추이성은 확장에 대해서 필드가 추가됬을때, 해당 필드를 위해 equals를 오버라이딩하면 문제가된다고 했다.

```java
public class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;

        Point p = (Point) o;
        return p.x == x && p.y == y;
    }
}
```

```java
public class ColoredPoint extends Point {

    private final Color color;

    public ColoredPoint(int x, int y, Color color) {
        // 기본적인 확장으로 equals를 별도로 오버라이딩 하진 않았다.
        super(x, y);
        this.color = color;
    }
}
```
여기서 equals가 ColoredPoint의 새로운 필드를 수용하기 위해 다음과 같이 수정해버린다면

대칭성에 문제가 발생한다.

```java
public class ColoredPoint extends Point {

    private final Color color;

    public ColoredPoint(int x, int y, Color color) {
        // 기본적인 확장으로 equals를 별도로 오버라이딩 하진 않았다.
        super(x, y);
        this.color = color;
    }

    // 재정의 하게되면 대칭성에 문제가된다.
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColoredPoint))
            return false;

        return super.equals(o) && ((ColoredPoint) o).color == color;
    }
}
```
```java
public class Practice {
    public static void main(String[] args) {
        Point p = new Point(1,2);
        ColoredPoint cp = new ColoredPoint(1, 2, Color.RED);

        System.out.println(p.equals(cp)); // true
        System.out.println(cp.equals(p)); // false
    }
}
```

그렇다고 "ColoredPoint에 Point의 인스턴스의 경우에는 색상을 무시하고 비교하라" 는 추이성을 위반하게 된다.

```java
public class Practice {
    public static void main(String[] args) {
        ColoredPoint cp1 = new ColoredPoint(1, 2, Color.BLUE);
        Point p = new Point(1, 2);
        ColoredPoint cp = new ColoredPoint(1, 2, Color.RED);

        System.out.println(cp1.equals(p));
        System.out.println(p.equals(cp));
        System.out.println(cp1.equals(cp));
    }
}
```

결과적으로 보면 `확장된 클래스에서의 추가 필드를 equals규약을 만족시키면서 포함할 방법이 없다.` 라는것을 알 수 있다.

물론 자기자신 객체와 비교하기 위해서 아래와 같이 수정할 수 있다.

```java
@Override
public boolean equals(Object o) {
    if (o == null || o.getClass() != getClass())
        return false;

    Point p = (Point) o;
    return p.x == x && p.y == y;
}
```

이러면 해당 equals를 오버라이딩한 클래스 타입끼리만 비교가 가능하고 그 외에 확장된 클래스에 대해서 대체가 불가능해 진다.

즉, `리스코프 치환 원칙`을 위반하게 된다.

리스코프 치환 원칙에 대해서 간단하게만 알아보자면 

어떤 타입으로 메소드를 작동시키고 있을 때, 그 `하위타입으로 대체되더라도 똑같이 잘 작동`해야 한다는 특징이 있다.

그래서 위의 구현체는 `List`와 같은 컬렉션 프레임워크의 `contains`와 같이 인자의 값을 Object로 받아서 equals를 유도하는 내부구현에서 대칭성 문제가 발생한다.

그러면 추가필드에 대해선 영원히 답이없는걸까?

`합성`으로 해결하려고 한다면 어느정도 커버가 된다.

그러니까 상속으로 부모필드를 채우는것이 아닌, 부모 객체를 필드로 가지고 필드의 equals()를 호출하면 된다.

<a href="../src/item10/transitive/solution/">예제 코드</a>

### 결과적인 equals 메소드의 구현 방법

최종 결론으로는 VO가 아니라면 Object의 equals를 재정의 하지않는것이 좋다.

그러니까 물리적인 동치성으로 충분히 만족이 된다면말이다.

VO는 필드를 통한 논리적 동치성이 필요하므로 재정의 해야한다.

이러한 상황에서는 아래의 규약을 만족하도록 하자.

1. `==` 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다. (물리적 동치성)

2. `instanceof` 입력이 올바른 타입인지 확인한다.

    - 자기 자신과 비교하는것이 좋은데, 자기자신과 비교하도록 인자에서부터 강요하면 오버로딩이 된다. 
        
        - Data 클래스가 있다고 했을 때, 다음과 같이 선언하는 경우 오버라이딩이 안된다.

        - `public boolean equals(Data o)` 는 오버라이딩이다.

    - 그러면 equals를 인자를 기준으로 쓰는 메소드의 경우 오버로딩된 메소드를 호출할 수없다.

        - 위의 오버로딩을 사용하면 사실 `Data o.equals(new Data())`는 의도대로 오버로딩 메소드가 작동한다.

        - 하지만 `List<Data> list`에서 `list.contains(new Data())`를 한 경우에는 인자의 객체를 Object로 받아서 equals()를 호출하기에, `무조건 오버라이딩 메소드` 혹은 Object의 equals()를 호출하게 되어 대칭성이 깨진다.

    - 그래서 `if(!(obj instanceof Class))`를 꼭 검사해주도록 하자.
    

3. 입력을 올바른 타입으로 형변환 한다. 

4. 입력 객체와 자기 자신의 대응되는 '핵심' 필드들이 모두 일치하는지 하나씩 검사한다.

    - 인터페이스로 비교하는거라면 해당 값에 접근권한이 있는 메소드로 비교해야 할 것이다.

    - 이 핵심필드들에 대해서 형이 상당히 복잡하다면, 필드의 표준형을 저장해둔 후 표준형끼리 비교하면 경제적이다.

