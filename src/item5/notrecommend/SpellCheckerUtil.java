package com.khc.practice.effectivejava.ch02.item5.notrecommend;

import com.khc.practice.effectivejava.ch02.item5.resource.KoreanDictionary;
import com.khc.practice.effectivejava.ch02.item5.resource.Lexicon;

public class SpellCheckerUtil {

    private static final Lexicon dictionary = new KoreanDictionary();

    private SpellCheckerUtil() {}

    public static boolean check(String word){
        return false;
    }

    public static void main(String[] args) {
        SpellCheckerUtil.check("김회창");
    }
}
