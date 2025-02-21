package com.example;

public class Karte {

    String wert;
    String typ;
    String name;

    public Karte() {
    }

    public Karte(String wert, String typ, String name) {
        this.wert = wert;
        this.typ = typ;
        this.name = name;
    }

    public String getWert() {
        return wert;
    }

    public void setWert(String wert) {
        this.wert = wert;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
