package com.khc.practice.effectivejava.ch02.item10.transitive.solution;

import com.khc.practice.effectivejava.ch02.item10.transitive.Color;

import java.util.List;
import java.util.Set;

public class Practice {

    public static void main(String[] args) {
        List<ColoredPoint> pSet = List.of(
                new ColoredPoint(1, 2, Color.BLUE), new ColoredPoint(2, 3, Color.BLUE),
                new ColoredPoint(3, 4, Color.BLUE), new ColoredPoint(4, 5, Color.BLUE)
        );

        System.out.println(pSet.contains(new ColoredPoint(1, 2, Color.BLUE)));
    }
}
