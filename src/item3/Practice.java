package com.khc.practice.effectivejava.ch02.item3;


import com.khc.practice.effectivejava.ch02.item3.finals.Elvis;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Practice {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Elvis elvis = new Elvis(); // private 생성자이기 때문에 막혀있다.
        Elvis elvis1 = Elvis.INSTANCE;
        Elvis elvis2 = Elvis.INSTANCE;
        System.out.println(elvis1 == elvis2); // 같음이 보장된다.


        Constructor cons = elvis1.getClass().getDeclaredConstructor();
        cons.setAccessible(true); // true 의 경우 자바의 접근제한자를 무시해버린다.

        Elvis elvis3 =  (Elvis) cons.newInstance(); // 이러한 공격을 막으려면 private 생성자가 재 호출되려 할때 Exception을 일으키면 된다.
        System.out.println("elvis1: "+elvis1);
        System.out.println("elvis2: "+elvis2);
        System.out.println("elvis3: "+elvis3);

        // Elvis elvis = new Elvis(); // private 생성자이기 때문에 막혀있다.
//        Elvis elvis1 = Elvis.getInstance();
//        Elvis elvis2 = Elvis.getInstance();

//        System.out.println(elvis1 == elvis2);


    }
}
