package com.khc.practice.effectivejava.ch02.item3.staticFactory;

import java.util.*;

import java.util.HashSet;

public class Elvis<T> {

    private T field;

    private static final Elvis<Object> INSTANCE = new Elvis<>();

    private Elvis() {}


    public void setField(T t) {
        this.field = t;
    }
    public T getField() {
        return this.field;
    }

    public static <E> Elvis<E> getInstance() {
        return (Elvis<E>) INSTANCE;
    }
}

class Main{
    public static void main(String[] args) {
        Elvis<Object> elvis1 = Elvis.getInstance();
    }
}