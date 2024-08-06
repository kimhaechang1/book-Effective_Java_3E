package com.khc.practice.effectivejava.ch02.item5.recommend;

import com.khc.practice.effectivejava.ch02.item5.resource.EnglishDictionary;
import com.khc.practice.effectivejava.ch02.item5.resource.KoreanDictionary;
import com.khc.practice.effectivejava.ch02.item5.resource.Lexicon;

public class SpellChecker {

    private final Lexicon dictionary;

    public SpellChecker(Lexicon dictionary){
        this.dictionary = dictionary;
    }

    public boolean check(String word){
        return false;
    }

    public static void main(String[] args) {
        SpellChecker koreanSpellChecker = new SpellChecker(new KoreanDictionary());
        SpellChecker englishSpellChecker = new SpellChecker(new EnglishDictionary());
    }
}
