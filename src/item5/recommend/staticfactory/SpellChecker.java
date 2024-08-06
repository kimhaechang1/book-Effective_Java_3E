package com.khc.practice.effectivejava.ch02.item5.recommend.staticfactory;

import com.khc.practice.effectivejava.ch02.item5.resource.KoreanDictionary;
import com.khc.practice.effectivejava.ch02.item5.resource.Lexicon;

public class SpellChecker {

    private final Lexicon dictionary;

    private SpellChecker(Lexicon dictionary) {
        this.dictionary = dictionary;
    }

    public static SpellChecker of(Lexicon dictionary){
        return new SpellChecker(dictionary);
    }
}
