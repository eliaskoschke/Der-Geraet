package com.example;

public class Karte {

    int wert;
    String name;
    String typ;

    public Karte(int wert, String name, String typ) {
        this.wert = wert;
        this.name = name;
        this.typ = typ;
    }

    public int getWert() {
        return wert;
    }

    public void setWert(int wert) {
        this.wert = wert;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }
}
