package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class GameGraphics extends Application {

    private static HBox cardTable;
    private static ImageView faceDownCard;
    int mult = 2;
    private final int cardWidth = 150 * mult;
    private final int cardHeight = 225 * mult;
    private final int screenWidth = 955 * mult;
    private final int screenHeight = 582 * mult;
    private static Stage myStage;

    // Variablen, um den Status der Button-Klicks zu speichern
    private static boolean isRestartClicked = false;
    private static boolean isMenuClicked = false;

    public GameGraphics() {
    }

    @Override
    public void start(Stage primaryStage) {
        myStage = primaryStage;
        Platform.runLater(() -> {
            myStage.setHeight(screenHeight);
            myStage.setWidth(screenWidth);
        });
        show(myStage);
    }

    public void show(Stage primaryStage) {
        cardTable = new HBox();
        cardTable.setSpacing(20);
        cardTable.setPadding(new Insets(20));
        cardTable.setAlignment(Pos.CENTER);

        cardTable.setStyle("-fx-background-image: url('file:/home/pi/Main-Branch/Der-Geraet/src/main/resources/static/img/HintergrundTisch.png'); " +
                "-fx-background-size: cover;");

        StackPane root = new StackPane();
        root.getChildren().add(cardTable);

        Scene scene = new Scene(root, screenWidth, screenHeight);
        primaryStage.setTitle("BlackJack");
        primaryStage.setScene(scene);
        primaryStage.show();
//        new Thread(() -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            HashMap<String, String> map = new HashMap<>();
//            map.put("Spieler 2", "ist ein hurensohn");
//            map.put("Spieler 3", "ist auch ein hurensohn");
//            showGameResults(map);
//        }).run();
    }

    public void addBeginningCards() {
        Platform.runLater(() -> {
            faceDownCard = new ImageView(new Image("file:/home/pi/Main-Branch/Der-Geraet/src/main/resources/static/img/cards" + "/500.png"));
            faceDownCard.setFitWidth(cardWidth);
            faceDownCard.setFitHeight(cardHeight);
            cardTable.getChildren().add(faceDownCard);
        });
    }

    public void addBeginningCards(Karte card) {
        Platform.runLater(() -> {
            ImageView cardImageView = new ImageView(card.getBild());
            cardImageView.setFitWidth(cardWidth);
            cardImageView.setFitHeight(cardHeight);
            cardTable.getChildren().add(cardImageView);
        });
    }

    public void addCardToTable(Karte neueKarte) {
        Platform.runLater(() -> {
            neueKarte.castKartenObjectToBildId();
            ImageView cardImageView = new ImageView(neueKarte.getBild());
            cardImageView.setFitWidth(cardWidth);
            cardImageView.setFitHeight(cardHeight);
            cardTable.getChildren().add(cardImageView);
        });
    }

    public void removeFaceDownCard() {
        Platform.runLater(() -> {
            if (faceDownCard != null) {
                cardTable.getChildren().remove(faceDownCard);
                faceDownCard = null;
            }
        });
    }

    public void showGameResults(HashMap<String, String> results) {
        Platform.runLater(() -> {
            StackPane resultPane = new StackPane();
            resultPane.setStyle("-fx-background-color: black;");

            StringBuilder resultText = new StringBuilder();
            for (Map.Entry<String, String> entry : results.entrySet()) {
                resultText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }

            Label resultLabel = new Label(resultText.toString());
            resultLabel.setTextFill(Color.WHITE);
            resultLabel.setFont(new Font("Arial", 50));
            resultLabel.setTextAlignment(TextAlignment.CENTER);

            Button restartButton = new Button("Spiel Neustarten");
            Button menuButton = new Button("Zurück zum Menü");

            restartButton.setPrefWidth(300);
            restartButton.setPrefHeight(60);
            menuButton.setPrefWidth(300);
            menuButton.setPrefHeight(60);

            restartButton.setStyle("-fx-font-size: 20px;");
            menuButton.setStyle("-fx-font-size: 20px;");

            restartButton.setOnAction(e -> {
                isRestartClicked = true;
                GameGraphics gameGraphics = new GameGraphics();
                gameGraphics.start(myStage);
            });

            menuButton.setOnAction(e -> {
                isMenuClicked = true;
                GameFX gameFX = new GameFX();
                gameFX.start(myStage);
            });

            VBox vbox = new VBox(20, resultLabel, restartButton, menuButton);
            vbox.setAlignment(Pos.CENTER);

            resultPane.getChildren().add(vbox);

            Scene resultScene = new Scene(resultPane, screenWidth, screenHeight);

            Stage stage = (Stage) cardTable.getScene().getWindow();
            stage.setScene(resultScene);
        });
    }

    public boolean isRestartClicked() {
        return isRestartClicked;
    }

    public boolean isMenuClicked() {
        return isMenuClicked;
    }

    public void setRestartClicked(boolean restartClicked) {
        isRestartClicked = restartClicked;
    }

    public void setMenuClicked(boolean menuClicked) {
        isMenuClicked = menuClicked;
    }

    public void remove() {
        Platform.runLater(() -> {
            Stage stage = (Stage) cardTable.getScene().getWindow();
            stage.close();
        });
    }

    public void launchApp() {
        launch();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
