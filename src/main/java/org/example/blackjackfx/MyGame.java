package org.example.blackjackfx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.NotFoundException;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MyGame extends Application {
    private int spielbreite = 800;
    private int spielhohe = 970;
    private boolean takeButtonIsPressed = false;
    private boolean passButtonIsPressed = false;

    private Image karteUmgedreht;
    private Image karteUmgedrehtHand;
    private Image hintergrundBild;
    private int buttonbreite = 200;
    private int buttonHohe = 70;
    private Image buttonStay;
    private Image buttonHit;

    private Button takeButton;
    private Button passButton;
    private Button restartButton;

    private int counter = 0;

    private String ergebnis = "";
    private String ergebnisUhr = "";
    private String ausgabeComputer = "";
    private String ausgabeSpieler = "";

    private boolean spielerTurn = true;
    private final int hintegrundbildStartWert = -100;
    private int spielerHandWert = 0;
    private int computerHandWert = 0;
    private boolean spielVorbei = false;

    private ArrayList<Karte> computerHand = new ArrayList<>();
    private ArrayList<Karte> spielerHand = new ArrayList<>();

    private Pane root;
    private Text ergebnisText;
    private Text ergebnisUhrText;

     static MotorController controller = new MotorController();
     static ObjectMapper mapper = new ObjectMapper();

    @Override
    public void start(Stage primaryStage) throws NotFoundException, InterruptedException, JsonProcessingException {
        root = new Pane();
        Scene scene = new Scene(root, spielbreite, spielhohe);
        root.setPrefHeight(spielhohe);
        root.setPrefWidth(spielbreite);

        // Bilder laden
        hintergrundBild = new Image("file:/home/pi/BlackJackFX/Der-Geraet/src/main/resources/Bilder/HintergrundTisch.png");
        buttonStay = new Image("file:/home/pi/BlackJackFX/Der-Geraet/src/main/resources/Bilder/stay.png");
        buttonHit = new Image("file:/home/pi/BlackJackFX/Der-Geraet/src/main/resources/Bilder/holen.png");
        karteUmgedreht = new Image("file:/home/pi/BlackJackFX/Der-Geraet/src/main/resources/Bilder/UmgedrehteKarte.png",
                100, 150, false, true);
        karteUmgedrehtHand = new Image("file:/home/pi/BlackJackFX/Der-Geraet/src/main/resources/Bilder/Karten/UmgedrehteKarteHand.png",
                100, 150, false, true);
        // Hintergrundbild erstellen
        ImageView background = new ImageView(hintergrundBild);
        background.setX(hintegrundbildStartWert);
        background.setY(0);
        root.getChildren().add(background);

        // "Hit"-Button konfigurieren
        takeButton = new Button();
        takeButton.setGraphic(new ImageView(buttonHit));
        takeButton.setLayoutX(584);
        takeButton.setLayoutY(200);
        takeButton.setPrefSize(75, 75);
        takeButton.setOnAction(e -> {
            try {
                takebuttonFunktion();
            } catch (InterruptedException | NotFoundException | JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });

        // "Stay"-Button konfigurieren
        passButton = new Button();
        passButton.setGraphic(new ImageView(buttonStay));
        passButton.setLayoutX(35);
        passButton.setLayoutY(200);
        passButton.setPrefSize(75, 75);
        passButton.setOnAction(e -> {
            try {
                passButtonFunktion();
                System.out.println(computerHandWert + " spieler: " + spielerHandWert);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Restart-Button als einfacher roter Button mit Text "Restart"
        restartButton = new Button("Restart");
        restartButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 16;");
        restartButton.setLayoutX(400);
        restartButton.setLayoutY(135);
        restartButton.setPrefSize(buttonbreite, buttonHohe);
        restartButton.setOnAction(e -> {
            try {
                anfangsWerte();
            } catch (NotFoundException | InterruptedException | JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });
        if (spielVorbei) {
            restartButton.setVisible(true);
        } else {
            restartButton.setVisible(false);
        }
        // Textanzeigen konfigurieren
        ergebnisText = new Text();
        ergebnisText.setFill(Color.rgb(229, 190, 1));
        ergebnisText.setFont(Font.font(25));
        ergebnisText.setX(377);
        ergebnisText.setY(520);

        ergebnisUhrText = new Text();
        ergebnisUhrText.setFill(Color.rgb(229, 190, 1));
        ergebnisUhrText.setFont(Font.font(48));
        ergebnisUhrText.setX(370);
        ergebnisUhrText.setY(550);

        root.getChildren().addAll(takeButton, passButton, restartButton, ergebnisText, ergebnisUhrText);

        // Spiel-Schleife starten
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();

        // Spiel initialisieren
        spielStart();


        primaryStage.setScene(scene);
        Platform.runLater(() -> {
            primaryStage.setHeight(spielhohe);
            primaryStage.setWidth(spielbreite);
        });
        primaryStage.show();
    }

    private void update() {
        if (counter > 60) {
            counter = 0;
        }
        counter++;

        if (!spielVorbei) {
            updateGameState();
        } else {
            updateEndGameState();
        }

        updateCardDisplay();
        updateTexts();
    }

    private void updateGameState() {
        if (counter < 15) {
            ergebnisUhr = "Ergebnis 🕛";
        } else if (counter < 30) {
            ergebnisUhr = "Ergebnis 🕒";
        } else if (counter < 45) {
            ergebnisUhr = "Ergebnis 🕜";
        } else if (counter < 60) {
            ergebnisUhr = "Ergebnis 🕤";
        }
    }

    private void updateEndGameState() {
        ergebnisUhr = "";
        if ((spielerHandWert > computerHandWert && spielerHandWert < 21) || computerHandWert > 21 || (spielerHandWert == 21 && computerHandWert != 21)) {
            ergebnis = "Spieler hat gewonnen";
        } else if ((spielerHandWert < computerHandWert && computerHandWert < 21) || spielerHandWert > 21 || (computerHandWert == 21 && spielerHandWert != 21)) {
            ergebnis = "Spieler hat verloren";
        } else {
            ergebnis = "Unentschieden";
        }
        if (spielerHandWert == 21 || computerHandWert == 21) {
            ergebnis = "Black Jack!\n" + ergebnis;
        }

        restartButton.setVisible(true);
    }

    private void updateCardDisplay() {
        // Entferne alle ImageViews außer dem Hintergrundbild
        root.getChildren().removeIf(node -> node instanceof ImageView &&
                !((ImageView) node).getImage().equals(hintergrundBild));

        // Computer-Karten anzeigen
        if (!spielerTurn) {
            if (!computerHand.isEmpty()) {
            for (int i = 0; i < computerHand.size(); i++) {
                ImageView cardView = new ImageView(computerHand.get(i).bild);
                cardView.setX(291 + hintegrundbildStartWert + (i * 105));
                cardView.setY(215);
                Text computerText = new Text(291 + hintegrundbildStartWert + (i * 105), 215, String.valueOf(computerHand.get(i).wert));
                computerText.setText(computerHand.get(i).wert + " " + computerHand.get(i).typ);
                root.getChildren().add(cardView);
                root.getChildren().add(computerText);
            }
        } else {
                // Während des Spielers: Zeige nur die erste Karte und die Rückseite der zweiten Karte
                ImageView firstCard = new ImageView(computerHand.get(0).bild);
                firstCard.setX(291 + hintegrundbildStartWert - 25);
                firstCard.setY(215);
                firstCard.setFitHeight(150);
                firstCard.setFitWidth(100);


                ImageView backCard = new ImageView(karteUmgedrehtHand);
                backCard.setX(291 + hintegrundbildStartWert + 83);
                backCard.setFitHeight(150);
                backCard.setFitWidth(100);
                backCard.setY(215);

                root.getChildren().addAll(firstCard, backCard);
            }
            }
        if (!spielerHand.isEmpty()) {
            // Spieler-Karten anzeigen
            for (int i = 0; i < spielerHand.size(); i++) {
                ImageView cardView = new ImageView(spielerHand.get(i).bild);
                cardView.setX(291 + (i * 105));
                cardView.setY(730);
                Text spielerText = new Text(291 + (i * 105), 730, spielerHand.get(i).toString());
                spielerText.setText(spielerHand.get(i).wert + " " + spielerHand.get(i).typ);
                root.getChildren().add(cardView);
                root.getChildren().add(spielerText);
            }
        }
    }

    private void updateTexts() {
        ergebnisText.setText(ergebnis);
        ergebnisUhrText.setText(ergebnisUhr);
    }

    // --- Spiellogik-Methoden ---

    // Initialisiert bzw. startet ein neues Spiel
    private void spielStart() throws NotFoundException, InterruptedException, JsonProcessingException {
        computerHand.clear();

        computerHand.add(Karte.gibKarte(controller, mapper));
        computerHand.add(Karte.gibKarte(controller, mapper));

        spielVorbei = false;
        spielerTurn = true;
        spielerHandWert = 0;
        computerHandWert = 0;

        System.out.println(computerHand.get(0).wert);
        computerHandWert += computerHand.get(0).wert;
        if (computerHand.get(1).name.contains("Ass")) {
            if (computerHandWert + 11 > 21) {
                computerHand.get(1).wert = 1;
            } else {
                computerHand.get(1).wert = 11;
            }

        }
        computerHandWert += computerHand.get(1).wert;

        ausgabeComputer = computerHand.get(0).name + "-" + computerHand.get(0).typ;


        spielerHand.add(Karte.gibKarte(controller, mapper));
        spielerHand.add(Karte.gibKarte(controller, mapper));


        spielerHandWert += spielerHand.get(0).wert;

        if (spielerHand.get(1).name.contains("Ass")) {
            if (spielerHandWert + 11 > 21) {
                spielerHand.get(1).wert = 1;
            } else {
                spielerHand.get(1).wert = 11;
            }

        }

        spielerHandWert += spielerHand.get(1).wert;
        ausgabeSpieler = "";
        for (Karte karte : spielerHand) {
            ausgabeSpieler += karte.name + "-" + karte.typ + "\n";
        }
        if (spielerHandWert == 21) {
            spielVorbei = true;
            spielerTurn = false;
            ausgabeComputer = "";
            for (Karte karte1 : computerHand) {
                ausgabeComputer += karte1.name + "-" + karte1.typ + "\n";
            }
        }

    }

    // Aktion, wenn der "Hit"-Button gedrückt wird
    private void takebuttonFunktion() throws InterruptedException, NotFoundException, JsonProcessingException {
        if (!spielerTurn || spielVorbei) return;

        spielerHand.add(Karte.gibKarte(controller, mapper));
        spielerHandWert = countHand(spielerHand);

        // Falls der Spieler 21 erreicht oder überkauft, wird der Zug beendet
        if (spielerHandWert >= 21) {
            spielerTurn = false;
            computerTurn();
        }
    }

    // Aktion, wenn der "Stay"-Button gedrückt wird
    private void passButtonFunktion() throws InterruptedException {
        if (!spielerTurn || spielVorbei) return;
        spielerTurn = false;
        computerTurn();
    }

    // Simuliert den Zug des Computers
    private void computerTurn() {
        Thread computerTurnThread = new Thread(() -> {
            if (spielerHandWert < 21) {
                int i = 0;

                if (computerHand.get(1).name.contains("Ass")) {
                    if (computerHandWert + 11 > 21) {
                        computerHand.get(1).wert = 1;
                    } else {
                        computerHand.get(1).wert = 11;
                    }
                }
                ausgabeComputer = "";
                for (Karte karte1 : computerHand) {
                    ausgabeComputer += karte1.name + "-" + karte1.typ + "\n";
                }
                if (computerHand.get(1).wert + computerHand.get(0).wert > 17) {
                    spielVorbei = true;

                }
                while (true) {
                    ausgabeComputer = "";
                    for (Karte karte1 : computerHand) {
                        ausgabeComputer += karte1.name + "-" + karte1.typ + "\n";
                    }
                    if (spielVorbei) {
                        break;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Karte karte = computerHand.get(i);

                    if (computerHandWert < 17) {
                        Karte randomKarte = null;
                        try {
                            randomKarte = Karte.gibKarte(controller, mapper);
                        } catch (NotFoundException | InterruptedException | JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        computerHand.add(randomKarte);

                        if (computerHandWert >= 17) {
                            spielVorbei = true;
                        }
                    } else {
                        spielVorbei = true;
                        break;
                    }
                    ausgabeComputer = "";
                    for (Karte karte1 : computerHand) {
                        ausgabeComputer += karte1.name + "-" + karte1.typ + "\n";
                    }

                    i++;
                    computerHandWert = countHand(computerHand);

                }
            } else {
                spielVorbei = true;
            }
        });
        computerTurnThread.start();
    }


    // Zählt den Gesamtwert einer Hand
    private int countHand(List<Karte> kartenHand) {
        boolean sonderRegel = false;
        if (kartenHand.get(0).name.substring(kartenHand.get(0).name.indexOf(" ")+1).equals("Ass") || kartenHand.get(1).name.substring(kartenHand.get(1).name.indexOf(" ")+1).equals("Ass")) {
            sonderRegel = true;
        }
        ArrayList<Karte> listOfAss = new ArrayList<>();
        int handWert = 0;

        for (Karte karte : kartenHand) {
            if (karte.name.substring(karte.name.indexOf(" ")+1).equals("Ass")) {
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
        return handWert;

    }

    // Setzt die Spielwerte zurück und startet ein neues Spiel
    private void anfangsWerte() throws NotFoundException, InterruptedException, JsonProcessingException {
        spielerHand.clear();
        computerHand.clear();
        spielerHandWert = 0;
        computerHandWert = 0;
        counter = 0;
        ergebnis = "";
        ergebnisUhr = "";
        spielVorbei = false;
        spielerTurn = true;
        restartButton.setVisible(false);
        root.getChildren().removeIf(node ->(node instanceof Text)
                && (node != ergebnisText && node != ergebnisUhrText));
        spielStart();
    }

    public static void main(String[] args) {
        launch(args);
    }
}