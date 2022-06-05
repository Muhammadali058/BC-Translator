package com.braincoder.bctranslator.Models;

import java.io.Serializable;

public class Languages implements Serializable {
    int id;
    String language;
    String languageCode;

    public Languages() {
    }

    public Languages(String language) {
        this.language = language;
    }

    public Languages(String language, String languageCode) {
        this.language = language;
        this.languageCode = languageCode;
    }

    public Languages(int id, String language) {
        this.id = id;
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public String toString() {
        return this.language;
    }
}
