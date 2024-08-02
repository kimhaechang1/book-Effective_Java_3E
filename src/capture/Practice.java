package com.khc.practice.effectivejava.ch05.capture;

import java.util.ArrayList;
import java.util.List;

public class Practice {


    public static void main(String[] args) {
//        WildcardError instance = new WildcardError();
//        List<Integer> node = new ArrayList<>(List.of(10,20));
//        System.out.println("before" + node);
//        instance.foo(node);
//        System.out.println("after" + node);
        List<String> stringList = new ArrayList<>();
        stringList.add("Hello");

        List<?> wildcardList = stringList;

        System.out.println(wildcardList.get(0));
    }
}
