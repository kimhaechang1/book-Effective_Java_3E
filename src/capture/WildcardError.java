package com.khc.practice.effectivejava.ch05.capture;

import java.util.List;

public class WildcardError {

    void foo(List<?> i, List<?> i2) {
//        i.set(0, i.get(0));
        this.fooHelper(i);
    }

    private <T> void fooHelper(List<T> l) {
        l.set(0, l.get(l.size()-1));
    }
}
