package com.khc.practice.effectivejava.ch05.item26;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Practice {

    public static void unSafeAddElement(List list, Object o){
        list.add(o);
    }

    public static void safeAddElement(List<Object> list, Object o){
        list.add(o);
    }

    public static void unboundedGeneric(Set<?> set1, Set<?> set2){
        int result = 0;

        // set1.add(); capture of ?

        for(Object o1: set1){
            if(set2.contains(o1)){
                result++;
            }
        }
        System.out.println("중복원소 개수: "+result);
    }

    public static void unboundedGeneric2(Set set1, Set set2){
        int result = 0;

        for(Object o1: set1){
            if(set2.contains(o1)){
                result++;
            }
        }
        System.out.println("중복원소 개수: "+result);
    }

    public static void main(String[] args) {
//        List<String> list = new ArrayList<>();
//        safeAddElement(list, Integer.valueOf(2));
//        String s = list.get(0); // Runtime Exception 발생Integer를 String으로 형변환 하려 했으므로
        Set<Integer> set1 = new HashSet<>(Set.of(1, 2, 3, 4, 5));
        Set<String> set2 = new HashSet<>(Set.of("김", "회", "창"));
        unboundedGeneric2(set1, set2);
//        List<String> casted = Practice.<String>castMaster();

    }
    static final List<Object> INSTANCE = new ArrayList<>();
    static List<?> castMaster() {
        return (List<?>) INSTANCE;
    }
}
