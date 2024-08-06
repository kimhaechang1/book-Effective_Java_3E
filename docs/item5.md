## 자원을 직접 사용하지 말고 의존 객체 주입을 사용하라

자원을 직접 사용하지 말란말이 무슨말일까?

현재 예제 코드이다.

```java
public class SpellCheckerUtil {
    
    private static final Lexicon dictionary = new KoreanDictionary();
    
    private SpellCheckerUtil() {}
    
    public static boolean check(String word){
        return false;
    }

    public static void main(String[] args) {
        SpellCheckerUtil.check("김회창");
    }
}
```

흔히 어떤 클래스는 어떤 다른 타입의 객체를 필요로 하기도 한다.

여기서 말하는 `어떤 다른 타입의 객체` 바로 이것이 의존성이자 즉 자원이 된다.

그리고 위의 예시처럼 해당 객체가 `어떤 객체`인가에 따라 크게크게 `동작`이 달라질 수 있다.

그런 경우에서는 결국 의존하는 자원(객체)를 유연하게 바꿀 필요가 있다.

그런데 무식하게 `final`때고 교체하는 메소드를 호출하면 되지않을까 라는 생각을 할 수있다.

하지만 이는 `동시성 이슈`가 발생할 가능성이 크다.

결과적으로 **객체에 따라 다른 동작으로 변형되는 클래스가 있을 경우, 정적 유틸리티 혹은 싱글톤으로 만들어선 안된다.**

즉 위의 `SpellChecker`는 다음과 같아져야 한다.

```java
public class SpellChecker {
    
    private final Lexicon dictionary;
    
    public SpellChecker(Lexicon dictionary){
        this.dictionary = dictionary;
    }
    
    public boolean check(String word){
        return false;
    }

    public static void main(String[] args) {
        SpellChecker koreanSpellChecker = new SpellChecker(new KoreanDictionary());
        SpellChecker englishSpellChecker = new SpellChecker(new EnglishDictionary());
    }
}
```
위와같이 생성자에 의존성을 주입할 객체를 인자로 넘겨받도록 클래스를 설계하면

클래스 객체 생성시에 동적으로 의존성을 주입할 수 있게되고, 이때 선택을 할 수 있게 된다.

여기서 `Lexicon dictionary`에 `final`이 떼어지지 않은점에 주목하자. 즉 `불변 멤버변수`를 유지한다.

물론 원래 했었던 `정적 팩토리 메소드`나 `빌더`로도 충분히 위와같은 구조를 만들 수 있다.

```java
// 정적 팩토리 메소드 버전
public class SpellChecker {

    private final Lexicon dictionary;

    private SpellChecker(Lexicon dictionary) {
        this.dictionary = dictionary; 
    }

    public static SpellChecker of(Lexicon dictionary){
        return new SpellChecker(dictionary);
    }
}
```
```java
// 빌더 버전
public class SpellChecker {

    private final Lexicon dictionary;

    static class Builder {

        private final Lexicon dictionary;

        public Builder(Lexicon dictionary){
            this.dictionary = dictionary;
        }

        public SpellChecker build(){
            return new SpellChecker(this);
        }

    }

    public SpellChecker(Builder builder){
        dictionary = builder.dictionary;
    }


    public static void main(String[] args) {
        SpellChecker koreanSpellChecker = new SpellChecker.Builder(new KoreanDictionary()).build();
        SpellChecker englishSpellChecker = new SpellChecker.Builder(new EnglishDictionary()).build();
    }
}
```

### Supplier와 한정자를 활용한 인스턴스 팩토리

여기서 말하는 팩토리는 호출할 때 마다 특정 타입의 인스턴스를 반복해서 만들어주는 객체를 말한다.

이를 JAVA8에서 `Supplier<T>`를 사용하여 구현할 수 있다

그리고 `T` 타입에 대해서 추가적으로 한정자를 추가하여, 특정 타입 하위의 객체를 생성하는 팩토리로 만들 수 있다.

팩토리의 핵심은 결국 넣어줘야하는 객체의 결정을 클래스단위에서 결정하는것이 아니라 객체를 생성하는 단계에서 클라이언트가 선택하게 만드는 것이다.

```java
// Supplier와 wildcard를 활용한 Factory 적용
public class SpellChecker {

    private final Lexicon dictionary;

    private SpellChecker(Lexicon dictionary) {
        this.dictionary = dictionary;
    }

    public static SpellChecker of(Supplier<? extends Lexicon> dictionaryFactory){
        return new SpellChecker(dictionaryFactory.get());
    }

    public static void main(String[] args) {
        SpellChecker koreanSpellChecker = SpellChecker.of(KoreanDictionary::new);
        SpellChecker englishSpellChecker = SpellChecker.of(EnglishDictionary::new);
    }
}
```


