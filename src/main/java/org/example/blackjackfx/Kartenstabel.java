package org.example.blackjackfx;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kartenstabel {
    private List<Karte> karten;

    public Kartenstabel() {
        karten = new ArrayList<>();
        String[] typen = {"Herz", "Karo", "Pik", "Kreuz"};
        String[] symbole = {"❤️", "♦️", "♠️", "♣️"};

        for (int i = 0; i < 4; i++) {
            String typ = typen[i];
            String symbol = symbole[i];

            for (int j = 2; j <= 10; j++) {
                String name = j + symbol;
                String bildPfad = "file:D:/projekte/BlackJackFx/src/main/resources/Bilder/Karten/" + typ + j + ".png";
                Image bild = new Image(bildPfad, 100, 150, false, true);
                karten.add(new Karte(j, name, typ, bild));
            }

            karten.add(new Karte(10, "Bube" + symbol, typ, loadImage(typ, "Bube")));
            karten.add(new Karte(10, "Dame" + symbol, typ, loadImage(typ, "Dame")));
            karten.add(new Karte(10, "König" + symbol, typ, loadImage(typ, "König")));
            karten.add(new Karte(11, "Ass" + symbol, typ, loadImage(typ, "Ass")));
        }

        Collections.shuffle(karten); // Mischen des Decks
    }

    private Image loadImage(String typ, String name) {
        String bildPfad = "file:D:/projekte/BlackJackFx/src/main/resources/Bilder/Karten/" + typ + name + ".png";
        return new Image(bildPfad, 100, 150, false, true);
    }

    public Karte gibKarte() {
        if (karten.isEmpty()) {
            System.out.println("Deck ist leer. Wird neu gemischt.");
            return null;
        }
        return karten.remove(0);
    }

    public static class Karte {
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
    }
}
