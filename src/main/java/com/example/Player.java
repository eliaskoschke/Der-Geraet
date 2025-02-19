package com.example;

import java.util.ArrayList;

public class Player {
    private String id;
    private ArrayList<Karte> kartenhand;

    public Player(String id, ArrayList<Karte> kartenhand) {
        this.id = id;
        this.kartenhand = kartenhand;
    }

    public Player() {
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
}
