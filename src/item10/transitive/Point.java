package com.khc.practice.effectivejava.ch02.item10.transitive;

public class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /*@Override
    // 확장되면 확장된 필드를 고려할 수 없다.
    public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;

        Point p = (Point) o;
        return p.x == x && p.y == y;
    }*/

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass())
            return false;

        Point p = (Point) o;
        return p.x == x && p.y == y;
    }
}
