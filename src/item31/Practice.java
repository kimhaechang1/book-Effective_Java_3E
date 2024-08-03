package com.khc.practice.effectivejava.ch05.item31;

import java.util.List;

public class Practice {

    public static void main(String[] args) {
        List<Dolar> dolarList = List.of(new Dolar(15),new Dolar(1), new Dolar(55));
        Dolar max = Moneys.max(dolarList);
    }
}
