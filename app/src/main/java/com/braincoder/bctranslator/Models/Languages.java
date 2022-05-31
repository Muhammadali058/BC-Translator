package com.braincoder.bctranslator.Models;

public class Languages {
    int id;
    String language;

    public Languages() {
    }

    public Languages(String language) {
        this.language = language;
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

    @Override
    public String toString() {
        return this.language;
    }
}
