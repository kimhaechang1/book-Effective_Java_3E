package com.khc.practice.effectivejava.ch02.item9.missingstacktrace;

public class MyResource implements AutoCloseable {

    public void open() {
        System.out.println("열어요");
        throw new IllegalArgumentException("열다가 사망");
    }

    @Override
    public void close() {
        System.out.println("닫아요");
        throw new IllegalArgumentException("닫다가 사망");
    }
}


