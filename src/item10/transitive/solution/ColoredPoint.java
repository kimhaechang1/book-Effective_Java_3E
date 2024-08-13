package com.khc.practice.effectivejava.ch02.item10.transitive.solution;

import com.khc.practice.effectivejava.ch02.item10.transitive.Color;

import java.util.Objects;

public class ColoredPoint {

    private final Point point;
    private final Color color;

    public ColoredPoint(int y, int x, Color color) {
        this.point = new Point(y, x);
        this.color = Objects.requireNonNull(color);
    }

    public Point asPoint() {
        return this.point;
    }

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof ColoredPoint)){
            return false;
        }
        ColoredPoint cp = (ColoredPoint) o;
        return cp.point.equals(point) && cp.color.equals(color);
    }



}
