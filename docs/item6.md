## 불필요한 객체 생성을 피하라.

말그대로다. 재사용이 충분히 안전하다고 판단된다면 불필요한 객체생성을 피해라

물론 이 과정에서 얻는것은 성능상에 이점이다.

<a href="./item50">방어적 복사</a> 와는 좀 대조되는 이야기 일수도 있다.

하지만 목적자체가 방어적 복사는 어찌보면 안정성 입장에서 이점을 얻기위함이 크다. 즉 버그로 이어질 가능성이 크다.

따라서 방어적 복사를 해야하는데 안했을때의 피해는 불필요한 객체 생성으로 인한 피해보다 훨씬 크기 때문에, 방어적 복사가 필요하다면 성능은 일단 제쳐두고 하는것이 좋다.

### 불필요한 객체 생성 예시: new String() vs literal

<a href="https://inpa.tistory.com/entry/JAVA-%E2%98%95-String-%ED%83%80%EC%9E%85-%ED%95%9C-%EB%88%88%EC%97%90-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0-String-Pool-%EB%AC%B8%EC%9E%90%EC%97%B4-%EB%B9%84%EA%B5%90">new Stirng() vs literal</a>

String 객체를 만드는 방법은 크게 두가지가 있다.

```java
String str = new String("김회창");  // String 인스턴스 생성
String str2 = "김회창"              // 리터럴
```

대부분의 경우에서 아래를 사용하라고 하는 이유는

아래의 경우에서는 String의 불변을 최대한 활용하여 최초에 한번 Heap 메모리위의 `String constant pool`에 등록되고 

그 이후로는 같은 문자열임이 보장된다면(`equals`) 같은 참조값을 들고오기 때문이다.

이를 만족하는 메서드가 내부적으로 `intern`이라는 네이티브 메소드로서 JVM내에 `String constant pool`에 접근하고 위의 행동을 수행하는 역할을 담당한다.

```java
String s2 = new String("회");
String s3 = new String("회");
String s4 = s3.intern();
String s5 = "회";
System.out.println(s2 == s3); // false
System.out.println(s3 == s4); // false
System.out.println(s4 == s5); // true

String s6 = "김";
String s7 = "창";
String s8 = s6 + s5 + s7;
String s9 = "김회창";
System.out.println(s8 == s9); // false

String s10 = s9.intern();
System.out.println(s8 == s10); // false
System.out.println(s9 == s10); // true
```
따라서 String을 최대한 효율적으로 사용하기 위해서는 `literal`을 사용하는것이 추천된다.

### keySet은 매번 같은 Set을 노출시킨다.

keySet은 흔히 Map 컬렉션에서 key를 활용하여 뷰를 생성하기위한 객체이다.

내부적으로 구현을 보면 Map 객체마다 단 한번만 초기화 되도록 되어있다.

```java
public Set<K> keySet() {
    Set<K> ks = keySet;
    if (ks == null) {
        ks = new KeySet();
        keySet = ks;
    }
    return ks;
}
```

생각해보면 같은 컬렉션에 대한 뷰만을 제공하는것은 결국 논리적으로 해시맵을 쳐다보고있을 뿐이므로, 

keySet의 반환값에 변화가 생긴다면 여러 `keySet`이 모두 같은 변화를 가져야한다.

이에 따라 `keySet()`의 결과가 모두 동일해야 하는 이유가 된다.

```java
String str = "daffsf";
Map<String, Integer> map = new HashMap<>(
        Map.ofEntries(
                Map.entry("회창", 12324),
                Map.entry("김", 12)
        )
);
Set<String> set1 = map.keySet(); // 같은 객체를 바라보고있다.
Set<String> set2 = map.keySet();

set1.remove("회창");

System.out.println(set1.stream().toList());
System.out.println(set2.stream().toList());
System.out.println(set1 == set2);
```