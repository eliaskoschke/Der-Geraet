package org.example.blackjackfx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.NotFoundException;
import javafx.scene.image.Image;

public class Karte {
    int wert;
    String name;
    String typ;
    @JsonIgnore
    public Image getBild() {
        return bild;
    }
    @JsonIgnore
    public void setBild(Image bild) {
        this.bild = bild;
    }

    Image bild;

    public Image castKartenObjectToBildId() {
        // String idCSV = "";
        int idValue = 0;
        switch (typ) {
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
        switch (name.substring(name.indexOf(" ") + 1)) {
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
        //home/pi/BlackJackFX/Der-Geraet/src/main/resources/Karten/202.png
        System.out.println("file:/home/pi/BlackJackFX/Der-Geraet/src/main/resources/Karten/" + idValue + ".png");
        return new Image("file:/home/pi/BlackJackFX/Der-Geraet/src/main/resources/Karten/" + idValue + ".png");

    }

    public Karte(String wert, String typ, String name) {
        this.wert = Integer.parseInt(wert);
        this.name = name;
        this.typ = typ;
        this.bild = castKartenObjectToBildId();
    }

    public static Karte gibKarte(MotorController motorController, ObjectMapper mapper) throws NotFoundException, InterruptedException, JsonProcessingException {
        String jsonKarte = QRCodeScannerPi.scan();
        if (!jsonKarte.isEmpty()) {
            Karte karte = mapper.readValue(jsonKarte, Karte.class);
            karte.bild = karte.castKartenObjectToBildId();
            motorController.spin();
            Thread.sleep(200);
            return karte;
        }
        System.out.println("Es wuid null gekackt");
        return null;
    }
    public Karte() {
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
}