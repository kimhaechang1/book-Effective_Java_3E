package com.khc.practice.effectivejava.ch04.item18.decorator;

public interface DataSource {
    void writeData(String data);

    String readData();
}
