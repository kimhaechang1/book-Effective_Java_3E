package com.khc.practice.effectivejava.ch04.item18.composition;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstrumentedSet<E> {

    private final Set<E> set;

    private int addCount;

    public InstrumentedSet(Set<E> set) {
        this.set = set;
    }

    public InstrumentedSet(int initCap, float loadFactor) {
        set = new HashSet<>(initCap, loadFactor);
    }

    public boolean add(E e) {
        addCount++;
        return set.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return set.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }

    public static void main(String[] args) {
        InstrumentedSet<Integer> set = new InstrumentedSet<>(new HashSet<>());
        set.addAll(List.of(1,2,3,4,5));
        System.out.println(set.getAddCount());
    }
}
