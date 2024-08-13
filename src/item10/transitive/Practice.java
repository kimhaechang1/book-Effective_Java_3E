package com.khc.practice.effectivejava.ch02.item10.transitive;

public class Practice {
    public static void main(String[] args) {

        Point p = new Point(1,2);
        ColoredPoint cp = new ColoredPoint(1, 2, Color.BLUE);

        p.equals(cp);

        /* cp1 = new ColoredPoint(1, 2, Color.BLUE);
        Point p = new Point(1,2);
        ColoredPoint cp = new ColoredPoint(1, 2, Color.RED);

        System.out.println(cp1.equals(p));
        System.out.println(p.equals(cp));
        System.out.println(cp1.equals(cp));*/
    }
}
