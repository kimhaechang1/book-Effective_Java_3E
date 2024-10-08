package com.khc.practice.effectivejava.ch02.item12;

import com.khc.practice.modernjava.ch19.lazy.Empty;

import java.util.Arrays;
import java.util.EmptyStackException;

public class Stack implements Cloneable {

    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        this.elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();

        Object result = elements[--size];
        elements[size] = null; // 다쓴 참조 해제
        return result;
    }

    public Object peek() {
        if (size == 0)
            throw new EmptyStackException();

        return elements[size-1];
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }

    // 가변객체 참조로 인해 재앙이 되는 예시
    /*public Stack clone() throws CloneNotSupportedException{
        return (Stack) super.clone();
    }*/

    public Stack clone() {
        try {
            Stack result = (Stack) super.clone();
            result.elements = elements.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }


}
