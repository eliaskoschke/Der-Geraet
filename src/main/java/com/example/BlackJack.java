package com.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BlackJack extends Application {

    private HBox cardTable;
    private ImageView faceDownCard;
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
        karte.setBild(new Image("file:D:/DEV_Ausbildung_24/Der-Geraet-Maven/src/main/resources/static/img/cards/201.png"));
        show(primaryStage);
    }

    public void show(Stage primaryStage) {
        cardTable = new HBox();
        cardTable.setSpacing(20); // Abstand zwischen den Karten
        cardTable.setPadding(new Insets(20)); // Innenabstand um die Karten herum
        cardTable.setAlignment(Pos.CENTER); // Zentriert die Karten in der HBox

        // Hintergrundbild hinzufügen
        cardTable.setStyle("-fx-background-image: url('file:D:/DEV_Ausbildung_24/Der-Geraet-Maven/src/main/resources/static/img/HintergrundTisch.png'); " +
                "-fx-background-size: cover;");

        // Karte: ping - 2025-02-25 12:35:25.754335+00:00 links von der umgedrehten Karte hinzufügen
        ImageView cardImageView = new ImageView(karte.getBild());
        cardImageView.setFitWidth(cardWidth);
        cardImageView.setFitHeight(cardHeight);
        cardTable.getChildren().add(cardImageView);

        // Umgedrehte Karte hinzufügen
        faceDownCard = new ImageView(new Image("file:D:/DEV_Ausbildung_24/Der-Geraet-Maven/src/main/resources/static/img/cards/500.png"));
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

    public void addCardToTable(Karte karte) {
        ImageView cardImageView = new ImageView(karte.getBild());
        cardImageView.setFitWidth(cardWidth);
        cardImageView.setFitHeight(cardHeight);
        cardTable.getChildren().add(cardImageView);
    }

    public void removeFaceDownCard() {
        if (faceDownCard != null) {
            cardTable.getChildren().remove(faceDownCard);
            faceDownCard = null;
        }
    }

    public void remove() {
        Stage stage = (Stage) cardTable.getScene().getWindow();
        stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }




}