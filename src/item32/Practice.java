package com.khc.practice.effectivejava.ch05.item32;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Practice {

    public static void main(String[] args) {
        //dangerous(List.of("김회창"));


        // String[] arttributes1 = toArray("좋은", "빠른", "저렴한");

        // String[] arttibutes = pickTwo("좋은", "빠른", "저렴한");

        String s1 = "좋은";
        String s2 = "빠른";
        String s3 = "저렴한";
        // String[] results = pickTwo(s1, s2, s3);

        List<String> result = pickTwoList(s1, s2, s3);
        // List 에 List를 대입하므로 아무런 문제가 없다.
        System.out.println(result);
    }



    static void dangerous(List<String> ... stringLists){
        List<Integer> intList = List.of(42);
        Object[] objects = stringLists;
        objects[0] = intList;
        String s = stringLists[0].get(0);
    }

    static <T> T[] toArray(T ...args){
        return args;
    }
    static <T> T[] pickTwo(T a, T b, T c){
        switch(ThreadLocalRandom.current().nextInt(3)){
            case 0: return toArray(a, b);
            case 1: return toArray(a, c);
            case 2: return toArray(b, c);
        }
        throw new AssertionError();
    }

    static <T> List<T> pickTwoList(T a, T b, T c){
        switch(ThreadLocalRandom.current().nextInt(3)){
            case 0: return List.of(a, b);
            case 1: return List.of(a, c);
            case 2: return List.of(b, c);
        }
        throw new AssertionError();
    }



}
