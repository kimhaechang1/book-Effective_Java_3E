package com.khc.practice.effectivejava.ch02.item3.enumElvis;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Practice {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Elvis obj = Elvis.INSTANCE;

        Constructor<Elvis> elvisConstructor = (Constructor<Elvis>) obj.getClass().getDeclaredConstructor();
        Elvis obj2 = elvisConstructor.newInstance();
        System.out.println(obj2);
    }
}
