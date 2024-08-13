package com.khc.practice.effectivejava.ch02.item10.transitive;

public class SmellPoint extends Point {

    private final int z;

    public SmellPoint(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }
}
