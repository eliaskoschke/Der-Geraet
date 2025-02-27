package com.example;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.scene.image.Image;

public class Karte {

    int wert;
    String typ;
    String name;
    Image bild;

    public Karte() {
    }

    public Karte(String wert, String typ, String name) {
        this.wert = Integer.parseInt(wert);
        this.typ = typ;
        this.name = name;
        bild = castKartenObjectToBildId();
    }

    public int getWert() {
        return wert;
    }

    public void setWert(int wert) {
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
    @JsonIgnore
    public Image getBild() {
        return bild;
    }
    @JsonIgnore
    public void setBild(Image bild) {
        this.bild = bild;
    }

    public Image castKartenObjectToBildId(){
        String idCSV ="";

        int idValue = 0;
        switch (typ){
            case "Karo":
                idValue += 100;
                break;
            case "Pik":
                idValue += 200;
                break;
            case "Herz":
                idValue += 300;
                break;
            case "Kreuz":
                idValue += 400;
                break;
        }
        switch (name.substring(name.indexOf(" ")+1)){
            case "Ass":
                idValue += 1;
                break;
            case "Bube":
                idValue += 11;
                break;
            case "Dame":
                idValue += 12;
                break;
            case "König":
                idValue += 13;
                break;
            default:
                idValue += wert;
                break;
        }
        idCSV = "D:\\DEV_Ausbildung_24\\Der-Geraet-Maven\\src\\main\\resources\\static\\img\\cards\\"+String.valueOf(idValue) +".png";
        Image image = new Image(idCSV);
        return image;
    }
}
