package com.khc.practice.effectivejava.ch04.item18;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;


public class InstrumentedHashSet<E> extends HashSet<E> {

    private int addCount = 0;

    public InstrumentedHashSet() {

    }

    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }


    public static void main(String[] args) {
        InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
        s.addAll(List.of("틱", "탁탁", "펑"));
        /*

        메소드 호출과 달리 상속은 캡슐화를 깨뜨린다.

        캡슐화를 생각해보면 상태와 행위를 묶어놓는 데이터의 캡슐화가 있고, 다른 객체와 소통하기 위한 메소드를 공개하고
        자신이 자율적인 객체로서 책임을 수행하는데 사용하는 방법을 자유롭게 선택하는데 있어서는 외부로 공개되지 않는다.
        왜냐하면 책임을 수행하는데 중요한것은 메세지를 보낸 사람에게 알맞은 응답 메세지를 전달하기만하면 된다.

        그런데 부모 메소드의 변화를 주의깊게 신경써야하는 코드를 작성하고 있다면, 또한 해당 변화에대한 문서화가 충분치 않다면
        결국 부모메소드의 구현상황을 직접 봐야한다. 그래서 상속에 이어서 오버라이딩을 하는데 있어서 부모 메소드와 결합되어 있다면, 캡슐화가 깨질 수 있다.

        * s.addAll()을 호출 한 순간 오버라이드 된 addAll이 호출된다. 왜냐하면 인스턴스가 상속된 InstrumentedSet 이기 때문
        * 여기서 addCount가 인자로 넘어온 컬렉션 크기만큼 더해지고
        * 이 이후 부모 메서드의 addAll()을 호출하는데, 여기서 부모 메소드의 addAll()은 내부적으로 add를 호출하고,
        * add는 또다시 오버라이딩된 add를 호출하기에 매순간 addCount가 더해진다.
        * 그래서 처음에 size() (즉, 3) 만큼 더해지고 3개의 원소가 add()를 호출 (+ 1)하여 6이 된다.
        * 그렇다고 addAll 메소드를 재정의 하지않으면 정상작동 하는거 아니냐? 라고 할 수 있는데
        * 이는 결국 부모 메소드에 완전히 맡기는 셈이고, 부모메소드의 동작방식이 새로운 버전으로 릴리즈 되어 동작이 바뀌는 순간 다시 또 고려해야한다.
        *
        *
        * */
        System.out.println(s.getAddCount());
    }
}
