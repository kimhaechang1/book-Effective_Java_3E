package com.khc.practice.effectivejava.ch02.item10.symmetric;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CaseInsentiveString {

    private final String s;

    public CaseInsentiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    @Override
    // String 필드 비교도 할수있으니 허용해볼까?
    // 대칭성을 위배하는 잘못된 행동이다.
    public boolean equals(Object o) {
        if( o instanceof CaseInsentiveString)
            return s.equalsIgnoreCase(
                    ((CaseInsentiveString) o).s
            );

        if( o instanceof String ) {
            return s.equalsIgnoreCase((String) o);
        }

        return false;
    }

    /*public boolean equals(Object o) {
        return o instanceof CaseInsentiveString &&
                ((CaseInsentiveString) o).s.equalsIgnoreCase(s);
    }*/

    public static void main(String[] args) {
        CaseInsentiveString cis = new CaseInsentiveString("Polish");
        String s = "polish";

        System.out.println(cis.equals(s));

        System.out.println(s.equals(cis));
        // 대칭성을 위배한다.
        // 왜냐하면 String class 입장에서는 CaseInsentiveString이 뭔지도 모른다.
        // instanceof 에서 컷난다.
        // 그러니 알맞은 동치성을 줄려면 부여받을 클래스에게 부여하는것이 좋다.

        List<CaseInsentiveString> list = new ArrayList<>();
        list.add(cis);

        System.out.println(list.contains("Polish"));
        // 내부적으로 CaseInsentiveString 클래스의 재정의된 equals를 호출하기를 기대하지만
        // 인자로 들어온 값을 Object로 받아서 equals()를 호출한다. 따라서 String의 equals()를 호출하게 된다.
    }
}
