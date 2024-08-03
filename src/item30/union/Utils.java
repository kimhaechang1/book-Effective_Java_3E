package com.khc.practice.effectivejava.ch05.item30.union;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {

    public static Set nonGenericUnion(Set s1, Set s2){
        Set result = new HashSet(s1);
        result.addAll(s2);
        return result;
    }

    public static <E> Set<E> genericUnion(Set<E> s1, Set<E> s2){
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }

    public static <E> Set<E> wildcardUnion(Set<? extends E> s1, Set<? extends E> s2){
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }

    public static void main(String[] args) {
        Set<Double> set1 = Set.of(10.5,125.5,12.4);;
        Set<Integer> set2 = Set.of(1,5,6,7);
        Set<Number> result = wildcardUnion(set1, set2);

        for(Number value: result)
            System.out.println("value: "+value);
    }
}
