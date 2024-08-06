package com.khc.practice.effectivejava.ch02.item5.recommend.supplier;

import com.khc.practice.effectivejava.ch02.item5.resource.EnglishDictionary;
import com.khc.practice.effectivejava.ch02.item5.resource.KoreanDictionary;
import com.khc.practice.effectivejava.ch02.item5.resource.Lexicon;

import java.util.function.Supplier;

public class SpellChecker {

    private final Lexicon dictionary;

    private SpellChecker(Lexicon dictionary) {
        this.dictionary = dictionary;
    }

    public static SpellChecker of(Supplier<? extends Lexicon> dictionaryFactory){
        return new SpellChecker(dictionaryFactory.get());
    }

    public static void main(String[] args) {
        SpellChecker koreanSpellChecker = SpellChecker.of(KoreanDictionary::new);
        SpellChecker englishSpellChecker = SpellChecker.of(EnglishDictionary::new);
    }
}