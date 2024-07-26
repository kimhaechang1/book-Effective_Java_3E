package com.khc.practice.effectivejava.ch02.item3.finals;

public class Elvis {

    private static boolean flag;
    public static final Elvis INSTANCE = new Elvis();

    // private constructor 는 단 한번 호출된다.
    private Elvis(){
        // 리플렉션을 막을수는 있다.

        if (!flag) flag = true;
        else throw new RuntimeException("리플렉션 막기!");
    }

    public void leaveTheBuilding() {}
}
