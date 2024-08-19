package com.khc.practice.effectivejava.ch03.item14;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.*;

public class Practice {

    static class PhoneNumber {

        final int areaCode;

        final int prefix;

        final int lineNum;

        public PhoneNumber(int areaCode, int prefix, int lineNum) {
            this.areaCode = areaCode;
            this.prefix = prefix;
            this.lineNum = lineNum;
        }
    }

    static class Student implements Comparable<Student>{

        final int id;
        final String name;

        public Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /*
        부호를 생각하여야 하기에 오류를 범하기 쉽다.
        대신 JAVA7에 추가된 Integer.compare() 메소드를 사용하여 가독성을 높히자.
        @Override
        public int compareTo(Student o) {
            if (id > o.id) {
                return 1;
            } else if (id < o.id) {
                return -1;
            } else {
                return 0;
            }
        }*/

        @Override
        public int compareTo(Student o) {
            // JAVA 7부터 가능한 정수 기본타입 Wrapper 클래스의 정적 메소드 compare 사용
            return Integer.compare(this.id, o.id);
        }


        @Override
        public String toString() {
            return "Student{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }


    }

    static void useWrapperClasscompare() {
        PriorityQueue<Student> studentPQ = new PriorityQueue<>();
        studentPQ.add(new Student(1, "김회창"));
        studentPQ.add(new Student(2, "회창김"));

        System.out.println(studentPQ.peek());
    }

    static void bigDecimalNotTransitive() {
        BigDecimal bc = new BigDecimal("1.00");
        BigDecimal bc1 = new BigDecimal("1.0");

        // 우선 BigDecimal은 대표적으로  x.compareTo(y) == 0 이 !x.equals(y) 인 마지막 규약을 지키지않은 클래스임
        HashSet<BigDecimal> set = new HashSet<>(); // equals 를 사용함
        set.add(bc);
        set.add(bc1);
        System.out.println(bc.equals(bc1));
        System.out.println(set.size() == 2); // 그래서 이 두개가 다르다고 나옴

        TreeSet<BigDecimal> treeSet = new TreeSet<>(); // Comparator를 사용함
        System.out.println(bc.compareTo(bc1));
        System.out.println(bc1);
        treeSet.add(bc);
        treeSet.add(bc1);
        System.out.println(treeSet.size() == 1);
    }



    static <T> BiFunction<T, T, Boolean> test(ToIntFunction<? extends T> func) {
        // func의 타입추론이 capture of 로서 캡쳐링을 명확하게 해야함
        // ? extends T 는 T타입 이거나 T 보다 하위타입이 오긴 하는데 뭔지 모른다이다.
        // T가 만약 Number인데 ToIntFunction<Integer>이며 다운캐스팅이 말이안되기 때문에, 컴파일러가 애초에 막아버리는거다.
        ToIntFunction<T> func2 = (ToIntFunction<T>) func;
        return (c1, c2) -> c1.equals(func2.applyAsInt(c2));
    }

    static <T> void test2(List<? extends T> list) {
        for(T n: list){
            System.out.print(n+" -> ");
        }
    }

    static void practiceTypeInference() {
        //Boolean result = Practice.test((t) -> t + 10).apply(25, 15);
        //System.out.println(result);
        // 이건 됨, 명시적인 타입 위트니스를 줬기 때문에

        /*
        Boolean result1 = Practice.test((t) -> t + 10).apply(25, 15);
        System.out.println(result);*/
        // 이건 타입추론 능력부족으로 안됨
        List<Integer> list = new ArrayList<>(List.of(10, 20));
        test2(list);
        Boolean result2 = test((Number t) -> t.intValue() + 10).apply(25, 15);
        // 이건됨, 이미 추론된 func1을 사용하기에 문제없음
        System.out.println(result2);

    }

    static void practiceComparing() {
        Comparator<PhoneNumber> comparator = Comparator.<PhoneNumber>comparingInt((pn) -> pn.areaCode)
                .thenComparingInt((pn) -> pn.prefix)
                .thenComparingInt((pn) -> pn.lineNum);
    }

    static void overflowComparator() {

        Student student1 = new Student(Integer.MIN_VALUE, "김회창");
        Student student2 = new Student(Integer.MAX_VALUE, "회창김");
        Comparator<Student> overflowSafeComparator2 = Comparator.comparingInt((Student d1) -> d1.id);
        Comparator<Student> overflowComparator = (d1, d2) -> d1.id - d2.id;
        Comparator<Student> overflowSafeComparator = (d1, d2) -> Integer.compare(d1.id, d2.id);
        PriorityQueue<Student> pq = new PriorityQueue<>(overflowSafeComparator);
        pq.add(student1);
        pq.add(student2);
        System.out.println();
        System.out.println(pq.poll());
    }

    public static void main(String[] args) throws Exception {
        //bigDecimalNotTransitive();
        //useWrapperClasscompare();
        practiceTypeInference();
        //overflowComparator();
    }
}
