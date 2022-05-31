package com.braincoder.bctranslator;

import com.google.mlkit.nl.translate.TranslateLanguage;

public class HP {

    public static String[] languages = new String[]{
            "English",
            "Urdu",
    };

    public static String[] installedLanguages = new String[]{
            "English",
            "Urdu",
    };

    public static String getLanguageCode(String language){
        switch (language){
            case "English":
                return TranslateLanguage.ENGLISH;
            case "Urdu":
                return TranslateLanguage.URDU;
            default:
                return TranslateLanguage.ENGLISH;
        }
    }

}
