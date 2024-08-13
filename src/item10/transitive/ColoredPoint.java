package com.khc.practice.effectivejava.ch02.item10.transitive;

public class ColoredPoint extends Point {

    private final Color color;

    public ColoredPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }


    // 재정의 하게되면 대칭성에 문제가된다.
    /*@Override
    public boolean equals(Object o) {
        if (!(o instanceof ColoredPoint))
            return false;

        return super.equals(o) && ((ColoredPoint) o).color == color;
    }*/

    // 이렇게 재정의하면 추이성에 문제가 된다.
    /*@Override
    public boolean equals(Object o) {
        if( !(o instanceof Point))
            return false;

        if( !(o instanceof ColoredPoint)) {
            return super.equals(o);
        }

        return super.equals(o) && ((ColoredPoint) o).color == color;
    }*/
}
