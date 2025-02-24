package org.example.blackjackfx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.NotFoundException;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class BlackJack extends Application {
    private int spielbreite = 800;
    private int spielhohe = 970;
    private boolean spielerTurn = true;
    private boolean spielVorbei = false;
    private int spielerHandWert = 0;
    private int computerHandWert = 0;
    private ArrayList<Karte> computerHand = new ArrayList<>();
    private ArrayList<Karte> spielerHand = new ArrayList<>();
    private Pane root;
    private Text ergebnisText;
    private Text ergebnisUhrText;
    private Image karteUmgedrehtHand;
    private Image hintergrundBild;
    private Button takeButton;
    private Button passButton;
    private Button restartButton;
    private int counter = 0;

    static MotorController controller = new MotorController();
    static ObjectMapper mapper = new ObjectMapper();

    @Override
    public void start(Stage primaryStage) throws NotFoundException, InterruptedException, JsonProcessingException {
        root = new Pane();
        Scene scene = new Scene(root, spielbreite, spielhohe);
        hintergrundBild = new Image("file:/home/pi/BlackJackFX/Der-Geraet/src/main/resources/Bilder/HintergrundTisch.png");
        karteUmgedrehtHand = new Image("file:/home/pi/BlackJackFX/Der-Geraet/src/main/resources/Bilder/Karten/UmgedrehteKarteHand.png", 100, 150, false, true);

        ImageView background = new ImageView(hintergrundBild);
        root.getChildren().add(background);

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

        takeButton = new Button("Hit");
        takeButton.setLayoutX(584);
        takeButton.setLayoutY(200);
        takeButton.setOnAction(e -> {
            try {
                takebuttonFunktion();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        passButton = new Button("Stay");
        passButton.setLayoutX(35);
        passButton.setLayoutY(200);
        passButton.setOnAction(e -> {
            try {
                passButtonFunktion();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        restartButton = new Button("Restart");
        restartButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 16;");
        restartButton.setLayoutX(400);
        restartButton.setLayoutY(135);
        restartButton.setOnAction(e -> {
            try {
                resetGame();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(takeButton, passButton, restartButton, ergebnisText, ergebnisUhrText);

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();

        startGame();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void update() {
        counter = (counter + 1) % 60;
        if (!spielVorbei) {
            ergebnisUhrText.setText("Ergebnis 🕛".substring(0, counter / 15 * 5 + 9));
        }
    }

    private void startGame() throws NotFoundException, InterruptedException, JsonProcessingException {
        spielVorbei = false;
        spielerTurn = true;
        spielerHand.clear();
        computerHand.clear();

        spielerHand.add(Karte.gibKarte(controller, mapper));
        spielerHand.add(Karte.gibKarte(controller, mapper));
        computerHand.add(Karte.gibKarte(controller, mapper));
        computerHand.add(Karte.gibKarte(controller, mapper));
    }

    private void takebuttonFunktion() throws NotFoundException, InterruptedException, JsonProcessingException {
        if (!spielerTurn || spielVorbei) return;
        spielerHand.add(Karte.gibKarte(controller, mapper));
        if (countHand(spielerHand) >= 21) {
            spielerTurn = false;
            computerTurn();
        }
    }

    private void passButtonFunktion() throws InterruptedException, NotFoundException, JsonProcessingException {
        if (!spielerTurn || spielVorbei) return;
        spielerTurn = false;
        computerTurn();
    }

    private void computerTurn() throws InterruptedException, NotFoundException, JsonProcessingException {
        while (countHand(computerHand) < 17) {
            computerHand.add(Karte.gibKarte(controller, mapper));
        }
        spielVorbei = true;
    }

    private int countHand(ArrayList<Karte> hand) {
        return hand.stream().mapToInt(k -> k.wert).sum();
    }

    private void resetGame() throws NotFoundException, InterruptedException, JsonProcessingException {
        root.getChildren().clear();
        startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
