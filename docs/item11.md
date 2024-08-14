## equals를 재정의하려거든 hashCode도 재정의하라

**equals를 재정의한 크래스 모두에서 hashCode도 재정의해야 한다.**

이건 당연한 얘기처럼 받아들여야 하고, `hashCode`의 경우 `hash`를 사용하는 자료구조 `Set, Map`에서 해시 충돌을 야기시킬 수 있다.

논리적으로 같은 객체라면 같은 해시코드를 반환해야 한다.

그렇게 하기 위해서는 `hashCode`메소드를 재정의 해야하는데 다음과 같은 규칙을 따르면 된다.

- int 변수의 result를 선언한 후 첫번째 핵심필드를 "적절한 방법"으로 hash값을 계산하여 초기화 한다.

- 모든 핵심필드 f에 대하여 다음을 수행한다.

    - 기본타입 이라면 *Type*.hashCode(f) 를 수행하며 여기서 *Type*은 박싱 클래스를 말한다.

    - 참조타입의 경우 equals() 메소드에서 재귀적으로 호출하고 있는 필드라면 hashCode도 재귀적으로 호출하자. 계산이 복잡해질 것 같으면 이 필드의 표준 형을 만들어서 그 표준형의 hashCode를 호출한다. 

        - null 이면 전통적으로 0을 사용한다.

    - 필드가 배열이라면 핵심 원소 각각을 별도 필드처럼 다룬다.

        - 즉 핵심 원소들만을 사용해서 각 원소들의 해시값을 위의 방식으로 구하고, 아래의 연산법을 적용시킨다.

        - 모든 Array내의 원소가 핵심원소라면 Arrays.hashCode() 를 사용하자

        - 모든 Array내의 원소가 핵심원소가 없다면 0을 결과로 사용한다.


    - 연산법: 그전 필드까지의 연산결과 result에 대하여 31을 곱하고 추가 핵심필드의 해시값을 더하여 누적한다.

        - result = result * 31 + hash;

            - result: 이전까지의 hash 연산의 결과

            - hash: 지금 계산한 핵심필드의 hash 연산의 결과

예시 클래스로 다음이 있다고 했을때 `hashCode()`를 규칙에 맞춰 재정의한 결과이다.

```java
public final class PhoneNumber {

    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode   = rangeCheck(areaCode, 999, "지역코드");
        this.prefix     = rangeCheck(prefix, 999, "프리픽스");
        this.lineNum    = rangeCheck(lineNum, 9999, "가입자 번호");
    }

    private static short rangeCheck(int val, int max, String arg){
        if (val < 0 || val > max) {
            throw new IllegalArgumentException(arg + " " + val);
        }
        return (short) val;
    }

    @Override public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof PhoneNumber))
            return false;

        PhoneNumber pn = (PhoneNumber) o;
        return pn.lineNum == lineNum && pn.prefix == prefix && pn.areaCode == areaCode;
    }

    public int hashCode() {
        int result = Short.hashCode(this.lineNum);
        result = 31 * result + Short.hashCode(this.areaCode);
        result = 31 * result + Short.hashCode(this.prefix);
        return result;
    }
}
```

물론 `Objects` 클래스에 hash라는 static 메소드가 있긴 하지만, 입력인수를 받기 위해 배열을 만들고 기본타입이 있따면 박싱 언박싱 오버헤드도 발생하기 때문이다.

성능이 괜찮다면 `Objects.hash()`도 고려해봐도 된다.

```java
public int hashCode() {
    return Objects.hash(lineNum, areaCode, prefix);
}
```

그리고 `String` 같은 불변을 지원하는 객체이거나, hash를 만들어내는 함수의 비용이 크다면 캐싱을 고려해야 한다.

캐싱을 고려할때 추가적으로 지연 초기화전략을 사용해보는것도 하나의 방법이다.

지연 초기화 전략은 간단하게 처음 호출될때만 한번 연산하는것을 의미한다.

지연 초기화 전략을 사용한 `String` 클래스의 예시이다.

```java
public class String { 
    /** Cache the hash code for the string */
    private int hash; // Default to 0

    public int hashCode() {
        int h = hash;
        if (h == 0 && !hashIsZero) {
            h = isLatin1() ? StringLatin1.hashCode(value)
                            : StringUTF16.hashCode(value);
            if (h == 0) {
                hashIsZero = true;
            } else {
                hash = h;
            }
        }
        return h;
    }
}
```



