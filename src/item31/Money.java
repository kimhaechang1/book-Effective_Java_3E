package com.khc.practice.effectivejava.ch05.item31;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Money implements Comparable<Money>{
    int value;

    public Money(int value) {
        this.value = value;
    }

    @Override
    public int compareTo(Money o) {
        return this.value - o.value;
    }
}


class Moneys{
    private Moneys(){

    }
    private static final List<Money> collection = new ArrayList<>();

    public static <T extends Money> List<T> getSingletonGenericMoneyFactory(){
        return (List<T>) collection;
    }

    public static <E extends Comparable<? super E>> E max(Collection<? extends E> c){
        if(c.isEmpty()){
            throw new IllegalArgumentException();
        }

        E result = null;
        for (E e: c){
            if (result == null || e.compareTo(result) > 0){
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }
}

class Dolar extends Money{

    public Dolar(int value) {
        super(value);
    }

    public String toString(){
        return "[ value: "+this.value +" $ ]";
   }
}

class Won extends Money{


    public Won(int value) {
        super(value);
    }
    public String toString(){
        return "[ value: "+this.value +" 원 ]";
    }
}