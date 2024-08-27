## 추상 클래스보다는 인터페이스를 우선하라.

자바에서는 크게 두가지의 추상화 방법이 존재하는데, 추상 클래스(abstract)와 인터페이스이다.

큰 차이로 볼 수 있는건 추상클래스는 어디까지나 클래스이기 때문에 다중상속이 안된다는점

인터페이스의 경우에는 인터페이스끼리의 확장에서 다중상속을 지원한다는 점에 차이가 있다.

물론 인터페이스를 구현하는 구체 클래스는 다중 구현도 가능하다.

이는 `믹스인 인터페이스`으로 이어지는데, 

낱말 해석을 하자면

`믹스인`이란 프로그래머가 특정 코드를 다른 클래스에 삽입할 수 있도록 하는 프로그래밍 개념을 의미하고

`믹스인 프로그래밍`은 특정 클래스에 작성된 기능들을 다른 클래스와 혼합하는 개발 유형을 뜻한다. (<a href="https://ko.wikipedia.org/wiki/%EB%AF%B9%EC%8A%A4%EC%9D%B8">믹스인의 정의</a>)

그래서 객체지향 프로그래밍에서의 믹스인은 `상속의 개념 (부모-자식) 관계`를 가지는것이 아니라 `포함`으로 설명한다는것이 중요한 특징이다.

이를 자바언어에서는 인터페이스로 구현하여서 믹스인 인터페이스라고 부른다.

다음의 예시가 자바에서 믹스인을 통해 작곡가와 작곡자 인터페이스를 혼합한 싱어송 라이터를 만드는 예시이다.

```java
public interface Singer {}

public interface SongWriter {}

public interface SingerSongWriter extends Singer, SongWriter {
    // SingerSongWriter는 자바에서 믹스인 프로그래밍을 구현한 인터페이스를 통해 만든 예시
    // 결국 SingerSongWriter는 두 인터페이스의 기능을 포함하게 된다.
}
```

하지만 추상클래스로 동일한 수행을 하기 위해서는 여러 기능을 혼합한 클래스를 만들려면 엄청난 계층구조를 야기시킬 수 있다.

추상 클래스가 무조건적으로 쓰이지 않는다? 는 아니며 `인터페이스와 함께 골격 구현 클래스로서 사용`한다면 최종 구체클래스를 만드는 `클라이언트의 부담을 줄일 수 있다.`

### 디폴트 메소드

인터페이스가 이제 인스턴스 메소드로 디폴트 메소드를 가질 수 있다.

하지만 디폴트 메소드도 몇가지 규칙이 있다.

- 상속을 고려하기 위해 디폴트 메소드도 <a href="./item19.md">item19</a> 처럼 @ImplSpec 을 통해 문서화 해야한다.

- Object에 있는 메소드들은 디폴트 메소드로 제공해서는 안된다.

    - 일단 기본적으로 컴파일 에러가 발생하더라고


### 인터페이스와 추상 골격 구현 클래스

인터페이스에 너무많은 추상 메소드가 있어서, 이를 구현하는 구체클래스가 너무많은 (자신이 다 필요로 하지도 않는) 메소드들을 구현해야 할 때 

중간에 추상 클래스로서 추상 골격구현 클래스를 넣으면, 최하위 클래스에서 필요한 메소드만을 재정의 하여 사용할 수 있다.

그 방법은 다음과 같다.

- 우선 인터페이스내에 다른 메소드 구현에 사용되는 메소드(기반 메소드)들을 추려낸다. 

- 이 기반 메소드들을 사용하여 결과를 만들어내는 메소드를 디폴트 메소드로 정의하고, 기반 메소드들은 추상 골격구현 클래스에서의 추상 메소드가 된다.

주의 할 점으로는

- 인터페이스의 메소드가 모두 기반 혹은 디폴트가 된다면, 별도의 골격 구현 클래스를 만들 이유가 없다.

이렇게 인터페이스를 완성하고 나서 `나머지 메소드들을 추상 골격구현 클래스에서 구현`하면 된다.

물론 `기반이 되어 내려져온 몇가지 추상메소드들도 필요하다면 골격 구현 클래스에서 구현`하여도 된다.

인터페이스 Map이 있고, 그의 골격 구현 클래스는 AbstractMap이다.

Map의 get(), put() 과 같은 메소드는 다른 메소드들의 기반 메소드로서 추상 메소드로 남아 있고

반대로 이들을 사용하여 결과를 만드는 메소드들은 default로 제공하고 있다.

```java
// Map.java에서 발췌
default V computeIfPresent(K key,
        BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    Objects.requireNonNull(remappingFunction);
    V oldValue;
    if ((oldValue = get(key)) != null) {
        V newValue = remappingFunction.apply(key, oldValue);
        if (newValue != null) {
            put(key, newValue);
            return newValue;
        } else {
            remove(key);
            return null;
        }
    } else {
        return null;
    }
}
```

결국 최소단위 메소드는 기반 메소드가 되고 추상메소드로 냅둔다. 물론 필요하다면 골격 구현 클래스에서 구현하기도 한다.

```java
// Map.java에서 발췌
boolean containsKey(Object key);

// AbstractMap.java에서 발췌
public boolean containsValue(Object value) {
    Iterator<Entry<K,V>> i = entrySet().iterator();
    if (value==null) {
        while (i.hasNext()) {
            Entry<K,V> e = i.next();
            if (e.getValue()==null)
                return true;
        }
    } else {
        while (i.hasNext()) {
            Entry<K,V> e = i.next();
            if (value.equals(e.getValue()))
                return true;
        }
    }
    return false;
}
```

한편, 인터페이스 내의 다른 추상메소드를 적어도 하나를 사용하여 구현되어야할 메소드는 디폴트 메소드가 된다.

이런식으로 설계해두면 골격 구현 클래스만을 확장하는것으로 인터페이스 구현에 필요한 일이 확 줄어든다.

```java
class MyMap<K, V> extends AbstractMap<K, V> {

    // 실제로 필수로 구현해야할 추상메소드가 아래의 entrySet() 말고는 없다.
	@Override
	public Set<Entry<K, V>> entrySet() {
		return null;
	}
}
```

이러한 방식을 응용한 디자인패턴으로 `템플릿 메소드 패턴`이 있다.

템플릿 메소드 패턴은 간단하게 설명하자면

어떤 동작과정을 정의한 메소드가 있고, 그 메소드에서 호출하는 다른 메소드들은 용도에 맞게 새롭게 정의해야하는 경우에 사용된다.

예를들어 커피만드는 동작을 추상화하려 할때, 다른 메소드들을 호출하고 있는 경우 

```java
커피만들기 () {
    뜨거운 물 넣기 -> 기본 동작
    커피 콩 가공하여 추가하기 -> 커피 종류에 따라 달라짐
}


abstract class A {

    public Water getHotWater() {
        return new Water();
    }

    abstract public CoffeeBean getCoffeeBean();

    public void makeCoffe() {
        Water hotWater = getHotWater();
    }
}

class 카푸치노 extends A {

    @Override
    public CoffeeBean getCoffeeBean() {
        return new 카푸치노CoffeeBean();
    }
}

class 아메리카노 extends A {

    @Override
    public CoffeeBean getCoffeeBean() {
        return new 아메리카노CoffeeBean();
    }
}

```

이들을 추상 클래스로 만들고 내부 동작에 쓰이는 필수가 아닌 메소드들은 전부 추상 메소드화 시킨다.

공통 골격구현 클래스를 만든다는것을 포함하여 거의 똑같다.

### 단순 구현 (Simple implementation)

골격 구현의 작은 변종으로서, 상속을 위해 인터페이스를 구현한거지만 추상 클래스가 아니기에 그대로 쓸수도 있다.

예시로서 AbstractMap에 있는 SimpleEntry가 중첩 클래스로 public으로 되어있다.

AbstractEntry는 따로없고 Map.Entry의 기본 구현 클래스로 사용할 수 있다.






