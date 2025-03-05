package com.example;

import java.util.ArrayList;
import java.util.List;

public class Computer {
    ArrayList<Karte> dealerHand ;
    int dealerHandWert;
    final String id = "Dealer";

    public Computer() {
    }

    public Computer(ArrayList<Karte> dealerHand, int dealerHandWert) {
        this.dealerHand = dealerHand;
        this.dealerHandWert = dealerHandWert;
    }

    public ArrayList<Karte> getDealerHand() {
        return dealerHand;
    }

    public void setDealerHand(ArrayList<Karte> dealerHand) {
        this.dealerHand = dealerHand;
    }

    public int getDealerHandWert() {
        return dealerHandWert;
    }

    public void setDealerHandWert(int dealerHandWert) {
        this.dealerHandWert = dealerHandWert;
    }

    public void countHand() {
        List<Karte> kartenHand = dealerHand;
        boolean sonderRegel = false;
        if (kartenHand.get(0).name.substring(kartenHand.get(0).name.indexOf(" ")+1).contains("Ass") || kartenHand.get(1).name.substring(kartenHand.get(1).name.indexOf(" ")+1).contains("Ass")) {
            sonderRegel = true;
        }
        ArrayList<Karte> listOfAss = new ArrayList<>();
        int handWert = 0;

        for (Karte karte : kartenHand) {
            if (karte.name.substring(karte.name.indexOf(" ")+1).contains("Ass")) {
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
        dealerHandWert = handWert;
    }

    public void resetDealer(){
        dealerHand = new ArrayList<>();
        dealerHandWert = 0;
    }

    public String getId() {
        return id;
    }
}
