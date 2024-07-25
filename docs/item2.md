## 빌더를 고려해보자

정적 팩토리 메서드도 인스턴스를 효과적으로 생성할 수 있는 방법이지만

매개변수가 4개이상일 때에는 `빌더 패턴`을 고려해보자.

기존의 생성자를 활용한 점층적 생성자 패턴(`telescoping constructor pattern`)은 다양한 매게변수에 대해서 생성자만을 사용하여 선택적으로 값을 넣을 수 있지만

매개변수의 숫자에 따라 대응해야할 생성자가 많아지고, 사용자 입장에서는 여전히 매개변수의 개수로 인해 사용 오류가 발생할 여지가 있다.

```java
public class NutritionFacts {

    // telescoping_constructor_pattern: 점층적 생성자 패턴
    // 필요한 매게변수의 개수가 1개인 생성자 부터 N개인 생성자 개수까지 점층적으로 늘려가며 생성자를 만든다.

    private final int servingSize;  // (ml, 1회 제공량)
    private final int servings;     // (회, 총 n회 제공량)
    private final int calories;     // (1회 제공량당)
    private final int fat;          // (g/1회 제공량)
    private final int sodium;       // (mg/1회 제공량)
    private final int carbohydrate; // (g/1회 제공량)

    public NutritionFacts(int servingSize, int servings){
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories){
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat){
        this(servingSize, servings, calories, fat, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium){
        this(servingSize, servings, calories, fat, sodium, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate){
        this.servingSize    = servingSize;
        this.servings       = servings;
        this.calories       = calories;
        this.fat            = fat;
        this.sodium         = sodium;
        this.carbohydrate   = carbohydrate;
    }
}
```

다른 방법으로는 세터(`setter`)를 활용한 자바빈즈 패턴(`JavaBeans pattern`)이 있다.

### JavaBeans 

https://ko.wikipedia.org/wiki/%EC%9E%90%EB%B0%94%EB%B9%88%EC%A6%88

자바빈즈는 빌더형식의 개발도구에서 가시적으로 조작이 가능하고 또한 재사용이 가능한 소프트웨어 컴포넌트이다. 라는데

사실 이것보다는 지켜야할 규칙을 지킨 클래스면 자바빈즈가 된다.

- 클래스는 직렬화되어야 한다. (클래스의 상태를 지속적으로 저장 혹은 복원시키기 위해)

- 클래스는 기본 생성자를 가지고 있어야 한다.

- 클래스의 속성들은 `get`, `set` 혹은 표준 명명법을 따르는 메서드들을 사용해 접근할 수 있어야 한다.

- 클래스는 필요한 이벤트 처리 메서드들을 포함하고 있어야 한다.

즉, 자바빈즈 패턴은 만들고싶은 객체를 위한 클래스를 자바빈즈 규칙에 따르게 하는 것이다.

하지만 자바빈즈 규칙에 따르기 위해서는 객체의 필드에 변경가능성을 열어둘 수 밖에 없다. (`setter`를 통해 값을 넣기 때문)

이에 따라 객체의 일관성(`consistency`)를 유지하기 힘들고, 유지하기 위해서 객체를 불변으로 만드는 `freeze` 메서드를 별도로 만든다고 하여도

다른 사용자가 `setter`를 충분히 호출 후 완성된 객체에 `freeze`를 명시적으로 호출하였는지를 검사할 방법이 없다.

빌더 패턴은 이 두가지 패턴의 장점만을 들고서 만들 수 있다.

빌더 패턴의 핵심은 `private final`로 선언되어 있되, 내부클래스 `Builder`를 통해 필수값과 선택값을 분리하여 관리할 수 있으며

빌더 패턴은 **계층적으로 설계된 클래스와 함께 사용하기 매우 좋다**.

```java
package com.khc.practice.effectivejava.ch02.item2.builder;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public abstract class Pizza {

    public enum Topping { HAM, MUSHROOM, ONION, PEPPER, SAUSAGE }

    final Set<Topping> toppings;

    abstract static class Builder<T extends Builder<T>> {

        // 하위 타입 빌더 객체를 제네릭으로 넘겨받는 방식
        // 상한 경계를 둔 이유는, 다형성을 최대한 활용하기 때문에도 있고,
        // 궁극적인 목적은 self() 메서드에 달려있다.

        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);
        public T addTopping(Topping topping){
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        public abstract Pizza build();

        // 하위 클래스는 이 메서드를 재정의 하여
        // this를 반환하도록 해야 한다.

        protected abstract T self();
        // return this로 하지않고 self() 로 별도로 만든 이유는
        // 하위타입 구현객체가 부모 객체의 메소드를 호출하고서 Builder를 반환할 때, 그때 하위타입 객체 Builder로 이어져야 하므로
        // return this를 하게되면 부모타입 객체가 반환되어져서, 부모타입 메서드만 호출이 가능하게 된다.
    }
    Pizza(Builder<? extends Pizza.Builder> builder){
        toppings = builder.toppings.clone();
        // 복사하는 이유
        // 멀티 스레드 환경에서 불변식을 검사하기위해 준비중인 builder 객체속 topping 객체에 대해서
        // 그 찰나의 순간에 다른 스레드가 동일한 toppings 객체에 대해 수정이 발생할 수 있기 때문에
        // 그리고 clone 메소드의 경우 신중하게 사용해야 한다.
        // toppings는 Enum클래스이기 때문에 final 클래스여서 더이상 상속할 수 없다는 특징이 있으니깐 clone을 호출해도 상관없지만
        // 만약 다른 하위타입으로 상속할 여지가 있다는것은 적어도 1회이상 clone을 재정의했을 가능성이 있다는 것이므로, clone보다는 직접 방어적 복사를 하는게 좋다.
    }
}
```

```java
package com.khc.practice.effectivejava.ch02.item2.builder;

import java.util.Objects;

public class NyPizza extends Pizza{

    public enum Size { SMALL, MEDIUM, LARGE }
    private final Size size;

    public static class Builder extends Pizza.Builder<Builder>{

        private final Size size; // 필수값이므로 빌더 생성자에서 바로 호출되도록 한다.

        public Builder(Size size){
            this.size = Objects.requireNonNull(size);
        }

        @Override
        public NyPizza build() {
            return new NyPizza(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private NyPizza(Builder builder){
        super(builder);
        this.size = builder.size;
    }
}

```
```java
package com.khc.practice.effectivejava.ch02.item2.builder;

public class Calzone extends Pizza{

    private final boolean sauceInside;

    public static class Builder extends Pizza.Builder<Builder>{

        // 여기서 제네릭 타입 인자로 넘겨주는 Builder는 Calzone.Builder 라는걸 잊으면 안됨

        private boolean sauceInside = false;    // 필수 타입이 아니므로, 기본값을 부여하기 위해

        public Builder sauceInside(){
            this.sauceInside = true;
            return this;
        }

        // 하위타입 빌더를 상위타입으로 넘김으로서, 상위타입의 메소드를 하위타입의 객체에 실어담을 수 있다.
        @Override
        public Calzone build() {
            return new Calzone(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
    private Calzone(Builder builder){
        super(builder);
        // 부모 타입 객체의 필드를 채우는 용도로 넘기고
        // 자식 객체의 필드를 채우는건 별도로 진행
        this.sauceInside = builder.sauceInside;
    }
}

```

인상 깊은 부분은 추상 클래스 `Pizza`의 `static inner class Builder`에 대하여 제네릭 타입을 선언한 것이다.

이 제네릭 타입으로 인해 추상 클래스를 **상속받는 클래스에서 타입을 지정**하여 상위 클래스의 메소드를 호출하더라도 타입이 보존될 수 있다.

이는 곧 `self()` 메서드로도 이어지는 이야기이다. 마치 결정된 타입을 상위에게 전달해주는 느낌이다.(혹은 타입결정을 하위 객체에 넘긴느낌)

또한 최종 빌더객체를 필드에 상위 클래스 필드에 붙여넣는 과정에서 복사를 일으켰다.

이는 추후의 아이템 50번에서 나오는 얘기지만, 멀티스레드 환경에서 `불변식`을 검사하는데 있어서 변경이 발생하지 않기 위해서 라고 한다.

불변식(`invariant`)란 불변과는 다르게 변경의 여지는 두지만, 특정 제한된 조건내에서 변화를 인정하는 것을 의미하낟.