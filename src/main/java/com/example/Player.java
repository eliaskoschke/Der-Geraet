package com.example;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String id;
    private ArrayList<Karte> kartenhand;
    private int kartenhandWert;

    public Player(String id, ArrayList<Karte> kartenhand, int kartenhandWert) {
        this.id = id;
        this.kartenhand = kartenhand;
        this.kartenhandWert = kartenhandWert;
    }

    public Player() {
    }

    public int getKartenhandWert() {
        return kartenhandWert;
    }

    public void setKartenhandWert(int kartenhandWert) {
        this.kartenhandWert = kartenhandWert;
    }

    public String toString(){
        return id;
    }

    public Player(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Karte> getKartenhand() {
        return kartenhand;
    }

    public void setKartenhand(ArrayList<Karte> kartenhand) {
        this.kartenhand = kartenhand;
    }

    public void countHand() {
        if(kartenhand == null || kartenhand.isEmpty()){
            kartenhandWert = 0 ;
            return;
        }
        List<Karte> kartenHand = kartenhand;
        boolean sonderRegel = false;
        if (kartenHand.get(0).name.contains("Ass") || kartenHand.get(1).name.contains("Ass")) {
            sonderRegel = true;
        }
        ArrayList<Karte> listOfAss = new ArrayList<>();
        int handWert = 0;

        for (Karte karte : kartenHand) {
            if (karte.name.contains("Ass")) {
                listOfAss.add(karte);
            } else {
                handWert += karte.wert;
            }
        }
        if (listOfAss.size() > 1 && sonderRegel) {
            int i = 1;

            for (Karte karte : listOfAss) {
                int anzahlAsse = listOfAss.size() - i;
                i++;
                if (handWert + 11 + anzahlAsse > 21) {
                    karte.wert = 1;
                } else {
                    karte.wert = 11;
                }
                handWert += karte.wert;

            }
        } else {
            for (Karte karte : listOfAss) {

                if (handWert + 11 > 21) {
                    karte.wert = 1;
                } else {
                    karte.wert = 11;
                }
                handWert += karte.wert;

            }
        }
        kartenhandWert = handWert;
    }
    public void resetPlayer(){
        kartenhand = new ArrayList<>();
        kartenhandWert = 0;
    }
}
