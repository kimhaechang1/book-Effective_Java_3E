package com.khc.practice.effectivejava.ch02.item5.recommend.builder;

import com.khc.practice.effectivejava.ch02.item5.resource.EnglishDictionary;
import com.khc.practice.effectivejava.ch02.item5.resource.KoreanDictionary;
import com.khc.practice.effectivejava.ch02.item5.resource.Lexicon;

public class SpellChecker {

    private final Lexicon dictionary;

    static class Builder {

        private final Lexicon dictionary;

        public Builder(Lexicon dictionary){
            this.dictionary = dictionary;
        }

        public SpellChecker build(){
            return new SpellChecker(this);
        }

    }

    public SpellChecker(Builder builder){
        dictionary = builder.dictionary;
    }


    public static void main(String[] args) {
        SpellChecker koreanSpellChecker = new SpellChecker.Builder(new KoreanDictionary()).build();
        SpellChecker englishSpellChecker = new SpellChecker.Builder(new EnglishDictionary()).build();
    }
}
