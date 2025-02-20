package org.example.blackjackfx;

import javafx.scene.image.Image;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kartenstabel {
    List<Karte> karten;

    public Kartenstabel() {
        karten = new ArrayList<>();
        String[] typen = {"Karo", "Pik", "Herz", "Kreuz"};
        String[] symbole = {"♦️", "♠️", "❤️", "♣️"};

        for (int i = 0; i < 4; i++) {
            String typ = typen[i];
            String symbol = symbole[i];

            for (int j = 1; j <= 13; j++) {
                String name;
                int wert;

                if (j == 1) {
                    name = "Ass";
                    wert = 11;
                } else if (j >= 11) {
                    name = switch (j) {
                        case 11 -> "Bube";
                        case 12 -> "Dame";
                        case 13 -> "König";
                        default -> "";
                    };
                    wert = 10;
                } else {
                    name = String.valueOf(j);
                    wert = j;
                }

                String bildPfad = imgPath(i, j);
                Image bild = new Image(bildPfad, 100, 150, false, true);
                karten.add(new Karte(wert, name, typ, bild));
            }
        }

        Collections.shuffle(karten); // Mischen des Decks
    }

    private String imgPath(int i, int j) {
        int suitNumber = i + 1;

        String cardNumber;
        if (j < 10) {
            cardNumber = "0" + j;
        } else {
            cardNumber = "" + j;
        }

        // Zusammensetzen des kompletten Pfads, z.B. "file:.../Karten/101.png" für Karo Ass.
        return "file:D:/projekte/BlackJackFx/src/main/resources/Karten/" + suitNumber + cardNumber + ".png";
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
