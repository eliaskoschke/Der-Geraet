package org.example.blackjackfx;

import javafx.scene.image.Image;

public class Karte {
    int wert;
    String name;
    String typ;
    Image bild;

    public Karte(int wert, String name, String typ, Image bild) {
        this.wert = wert;
        this.name = name;
        this.typ = typ;
        this.bild = bild;
    }

    public String getTyp() {
        return typ;
    }

    public int getWert() {
        return wert;
    }

    public String getName() {
        return name;
    }

    public Image getBild() {
        return bild;
    }
}
