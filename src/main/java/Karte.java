public class Karte {

    String wert;
    String name;
    String typ;
    //PImage image;

    public Karte(String wert, String typ, String name) {
        this.wert = wert;
        this.name = name;
        this.typ = typ;

        /*
        PImage spriteSheet;
        int cardWidth, cardHeight;
        int rows = 4; // Anzahl der Reihen im Sprite Sheet
        int cols = 13;

        cardWidth = spriteSheet.width / cols ;
        cardHeight = spriteSheet.height / rows;

        int zeile =0;
        switch (typ){
            case "Karo":
                break;
            case "Pik":
                zeile = 1;
                break;
            case "Herz":
                zeile = 2;
                break;
            case "Kreuz":
                zeile = 3;
                break;
        }
        int spalte =0;
        switch (name){
            case "Ass":
                break;
            case "König":
                spalte = 1;
                break;
            case "Dame":
                spalte = 2;
                break;
            case "Bube":
                spalte = 3;
                break;
            default:
                spalte = 14 - wert;
                break;
        }
        this.image = spriteSheet.get(cardWidth*spalte , cardHeight*zeile, cardWidth, cardHeight);
        */

    }

    public Karte() {
    }

    public String getWert() {
        return wert;
    }

    public void setWert(String wert) {
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
