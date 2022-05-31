package com.braincoder.bctranslator.Models;

public class LanguageHolder {
    int image;
    String language;

    public LanguageHolder() {
    }

    public LanguageHolder(int image, String language) {
        this.image = image;
        this.language = language;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
