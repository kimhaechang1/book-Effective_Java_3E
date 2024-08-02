package com.khc.practice.effectivejava.ch05.capture;

public class Node<T>{
    private T first;
    private T second;



    public Node(T f, T s) {
        this.first = f;
        this.second = s;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(T first) {
        this.first = first;
    }

    public T getFirst(){
        return this.first;
    }
    public T getSecond(){
        return this.second;
    }

    public int method(){
        return 0;
    }

    public String toString(){
        return "[ first: "+first+" second: "+second+" ]";
    }
}