package com.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BlackJack extends Application {

    private static HBox cardTable;
    private static ImageView faceDownCard;
    private final int cardWidth = 150;
    private final int cardHeight = 225;
    private final int screenWidth = 1024;
    private final int screenHeight = 600;
    private Karte karte = new Karte();

    public BlackJack() {
    }

    @Override
    public void start(Stage primaryStage) {
        // Beispielkarte erstellen
        Platform.runLater(()->{
            primaryStage.setHeight(screenHeight);
            primaryStage.setWidth(screenWidth);
        });
        show(primaryStage);
    }

    public void show(Stage primaryStage) {
        karte = new Karte("4", "Herz", "Herz 4");
        karte.castKartenObjectToBildId();
        cardTable = new HBox();
        cardTable.setSpacing(20); // Abstand zwischen den Karten
        cardTable.setPadding(new Insets(20)); // Innenabstand um die Karten herum
        cardTable.setAlignment(Pos.CENTER); // Zentriert die Karten in der HBox

        // Hintergrundbild hinzufügen
        cardTable.setStyle("-fx-background-image: url('file:/home/pi/Main-Branch/Der-Geraet/src/main/resources/static/img/HintergrundTisch.png'); " +
                "-fx-background-size: cover;");

        // Karte links von der umgedrehten Karte hinzufügen
        ImageView cardImageView = new ImageView(karte.getBild());
        cardImageView.setFitWidth(cardWidth);
        cardImageView.setFitHeight(cardHeight);
        cardTable.getChildren().add(cardImageView);

        // Umgedrehte Karte hinzufügen
        faceDownCard = new ImageView(new Image("file:/home/pi/Main-Branch/Der-Geraet/src/main/resources/static/img/cards/500.png"));
        faceDownCard.setFitWidth(cardWidth);
        faceDownCard.setFitHeight(cardHeight);
        cardTable.getChildren().add(faceDownCard);

        StackPane root = new StackPane();
        root.getChildren().add(cardTable);

        Scene scene = new Scene(root, screenWidth, screenHeight);
        primaryStage.setTitle("BlackJack");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void addCardToTable(Karte neueKarte) {
        Platform.runLater(()-> {
            neueKarte.castKartenObjectToBildId();
            ImageView cardImageView = new ImageView(neueKarte.getBild());
            cardImageView.setFitWidth(cardWidth);
            cardImageView.setFitHeight(cardHeight);
            cardTable.getChildren().add(cardImageView);
        });
    }

    public void removeFaceDownCard() {
        Platform.runLater(()->{
            if (faceDownCard != null) {
                cardTable.getChildren().remove(faceDownCard);
                faceDownCard = null;
            }
        });
    }

    public void remove() {
        Stage stage = (Stage) cardTable.getScene().getWindow();
        stage.close();
    }

    public void launchApp() {
        launch();
    }

    public static void main(String[] args) {
        launch(args);
    }




}
