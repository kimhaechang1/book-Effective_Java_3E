package com.khc.practice.effectivejava.ch02.item12.calloverridemethod;

public class Practice {
    public static void main(String[] args) {
        SubClass original = new SubClass(42, "initialized");
        original.printState();
        SubClass cloned = original.clone();
        // reset메소드가 의도치 않게 작동하게됨
        cloned.printState();
    }
}
