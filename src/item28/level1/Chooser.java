package com.khc.practice.effectivejava.ch05.item28.level1;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chooser {

    private final Object[] choiceArray;

    public Chooser(Collection choices) {
        choiceArray = choices.toArray();
    }

    public Object choose(){
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
}
