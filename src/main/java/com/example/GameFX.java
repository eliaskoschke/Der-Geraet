package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GameFX extends Application {
    private static final String BACKGROUND_COLOR = "rgb(36, 43, 66)";
    private static final String BUTTON_DEFAULT_COLOR = "rgb(70, 103, 210)";
    private static final String BUTTON_HOVER_COLOR = "rgb(111, 137, 220)";
    private static final String BUTTON_ACTIVE_COLOR = "rgb(45, 77, 185)";

    static List<Boolean> seats = new ArrayList<>(6);
    static List<Circle> circles = new ArrayList<>(6);
    private static VBox seatLayout;
    private Button startButton;
    private static Gamemode currentGame;
    private static boolean gameStarted = false;

    public GameFX(){
        seats = new ArrayList<>(6);
        circles = new ArrayList<>(6);
        VBox seatLayout;
        gameStarted = false;
    }

    @Override
    public void start(Stage primaryStage) {
        // PiController piController = new PiController();
        // Hauptlayout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        root.setPadding(new Insets(20));

        // Buttons erstellen
        Button blackJackBtn = createStyledButton("Black Jack");
        Button pokerBtn = createStyledButton("Poker");
        Button luegeBtn = createStyledButton("Lüge");

        // Hover-Effekte für die Buttons
        addHoverEffect(blackJackBtn);
        addHoverEffect(pokerBtn);
        addHoverEffect(luegeBtn);

        // Button Container im oberen Bereich
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 0, 20, 0));
        buttonBox.getChildren().addAll(blackJackBtn, pokerBtn, luegeBtn);
        root.setTop(buttonBox);

        // Sitzplatz Layout im Zentrum
        seatLayout = new VBox(20);
        seatLayout.setAlignment(Pos.CENTER);
        seatLayout.setVisible(false);
        root.setCenter(seatLayout);

        // Start Button im unteren Bereich
        startButton = createStyledButton("Start");
        addHoverEffect(startButton);
        startButton.setVisible(false);

        VBox bottomBox = new VBox(20);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));
        bottomBox.getChildren().add(startButton);
        root.setBottom(bottomBox);

        // Event-Handler für den Start-Button
        //startButton.setOnAction(e -> openGameScene(primaryStage));

        // Button Event Handler
        blackJackBtn.setOnAction(e -> {
            showSeats("Black Jack");
            toggleButtonStyle(blackJackBtn);
            resetOtherButtons(blackJackBtn, pokerBtn, luegeBtn);
        });

        pokerBtn.setOnAction(e -> {
            showSeats("Poker");
            toggleButtonStyle(pokerBtn);
            resetOtherButtons(pokerBtn, blackJackBtn, luegeBtn);
        });

        luegeBtn.setOnAction(e -> {
            showSeats("Lüge");
            toggleButtonStyle(luegeBtn);
            resetOtherButtons(luegeBtn, blackJackBtn, pokerBtn);
        });

        startButton.setOnAction(e -> {
            gameStarted = true;
            GameGraphics gameGraphics = new GameGraphics();
            gameGraphics.start(primaryStage);
        });

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Der Gerät");
        primaryStage.setScene(scene);

        // Fenster maximieren und Minimalgröße setzen
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        Platform.runLater(() -> {
            primaryStage.setWidth(1910);
            primaryStage.setHeight(1164);
        });
    }

    private void addHoverEffect(Button button) {
        button.setOnMouseEntered(e -> {
            if (!button.getStyle().contains(BUTTON_ACTIVE_COLOR)) {
                button.setStyle(getHoverButtonStyle());
            }
        });
        button.setOnMouseExited(e -> {
            if (!button.getStyle().contains(BUTTON_ACTIVE_COLOR)) {
                button.setStyle(getDefaultButtonStyle());
            }
        });
    }

    private void resetOtherButtons(Button activeButton, Button... otherButtons) {
        for (Button button : otherButtons) {
            button.setStyle(getDefaultButtonStyle());
        }
    }



    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(getDefaultButtonStyle());
        return button;
    }

    private String getDefaultButtonStyle() {
        return String.format("""
                    -fx-background-color: %s;
                    -fx-text-fill: white;
                    -fx-font-size: 18px;
                    -fx-font-weight: Bold;
                    -fx-min-width: 180px;
                    -fx-min-height: 60px;
                    -fx-background-radius: 30;
                    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);
                """, BUTTON_DEFAULT_COLOR);
    }

    private String getHoverButtonStyle() {
        return String.format("""
                    -fx-background-color: %s;
                    -fx-text-fill: white;
                    -fx-font-size: 18px;
                    -fx-font-weight: Bold;
                    -fx-min-width: 180px;
                    -fx-min-height: 60px;
                    -fx-background-radius: 30;
                    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 8, 0, 0, 0);
                """, BUTTON_HOVER_COLOR);
    }

    private String getActiveButtonStyle() {
        return String.format("""
                    -fx-background-color: %s;
                    -fx-text-fill: white;
                    -fx-font-size: 18px;
                    -fx-font-weight: Bold;
                    -fx-min-width: 180px;
                    -fx-min-height: 60px;
                    -fx-background-radius: 30;
                    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);
                """, BUTTON_ACTIVE_COLOR);
    }

    private void toggleButtonStyle(Button button) {
        if (!button.getStyle().contains(BUTTON_ACTIVE_COLOR)) {
            button.setStyle(getActiveButtonStyle());
        } else {
            button.setStyle(getDefaultButtonStyle());
        }
    }

    private void showSeats(String game) {
        switch (game){
            case "Black Jack" ->{
                currentGame = Gamemode.BLACKJACK;
            }
            case "Poker" ->{
                currentGame = Gamemode.POKER;
            }
        }

        seats.clear();
        for (int i = 0; i < 6; i++) {
            seats.add(false); // All seats start as empty
        }

        seatLayout.getChildren().clear();

        Text title = new Text(game + " - Sitzplätze");
        title.setStyle("-fx-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        // Seat container
        Pane seatsPane = new Pane();
        seatsPane.setPrefSize(700, 280);

        // Bind the size of the Pane to the size of the window
        seatsPane.prefWidthProperty().bind(seatLayout.widthProperty());
        seatsPane.prefHeightProperty().bind(seatLayout.heightProperty().subtract(50)); // Adjust for title

        // Use bindings for dynamic positioning
        double centerX = seatsPane.getPrefWidth() / 2;
        double centerY = seatsPane.getPrefHeight() / 2 - 50; // Tisch weiter nach oben verschieben
        double radius = Math.min(seatsPane.getPrefWidth(), seatsPane.getPrefHeight()) / 2.5; // Größerer Radius für mehr Abstand

        for (int i = seats.size()-1; i >=0 ; i--) {
            // Spiegel den Winkelbereich, um die Plätze unterhalb des Tisches zu positionieren
            double angle = Math.PI * (1.0 + i / (double) (seats.size() - 1)) + Math.PI;

            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            VBox seatContainer = new VBox(8);
            seatContainer.setAlignment(Pos.CENTER);

            Circle seat = new Circle(46);
            seat.setFill(seats.get(i) ? Color.RED : Color.GREEN);

            Text seatNumber = new Text("Platz " + (seats.size() - i));
            seatNumber.setStyle("-fx-fill: white; -fx-font-size: 14px; -fx-font-weight: Bold;");

            int finalI = i;
            seat.setOnMouseClicked(e -> {
                seats.set(finalI, !seats.get(finalI));
                seat.setFill(seats.get(finalI) ? Color.RED : Color.GREEN);
            });

            seatContainer.getChildren().addAll(seat, seatNumber);

            // Bind the position of the seat containers
            seatContainer.layoutXProperty().bind(seatsPane.widthProperty().multiply(x / seatsPane.getPrefWidth()).subtract(seat.getRadius()));
            seatContainer.layoutYProperty().bind(seatsPane.heightProperty().multiply(y / seatsPane.getPrefHeight()).subtract(seat.getRadius()));

            seatsPane.getChildren().add(seatContainer);
        }

        // Erstelle den Tischkreis
        Circle table = new Circle();
        table.setFill(Color.rgb(70, 103, 210, 0.3));
        table.setStroke(Color.rgb(111, 137, 220, 0.5));
        table.setStrokeWidth(2);

        // Binde den Radius des Kreises an die kleinere Dimension des Pane
        table.radiusProperty().bind(seatsPane.widthProperty().multiply(0.1)); // 10% der Breite

        // Binde die Position des Kreises an die Mitte des Pane
        table.centerXProperty().bind(seatsPane.widthProperty().divide(2));
        table.centerYProperty().bind(seatsPane.heightProperty().divide(2).subtract(50)); // Tisch weiter nach oben verschieben

        seatsPane.getChildren().add(0, table);
        // Arrange everything
        seatLayout.getChildren().addAll(title, seatsPane);
        seatLayout.setVisible(true);
        startButton.setVisible(true);
    }

    public static void updateSeatColor(List<String> id) {
        for (int i = 0; i < id.size(); i++) {
            int seat = Integer.parseInt(id.get(i)) -1;
            circles.get(seat).setFill(Color.RED);
        }
    }

    public void launchApp() {
        launch();
    }

    private void openGameScene(Stage primaryStage) {
        // Erstelle ein neues Layout mit blauem Hintergrund
        StackPane gameLayout = new StackPane();
        gameLayout.setStyle("-fx-background-color: blue;");

        // Erstelle eine neue Szene mit dem Layout
        Scene gameScene = new Scene(gameLayout, 800, 600);

        // Setze die neue Szene auf der primären Bühne
        primaryStage.setScene(gameScene);
        GameGraphics gameGraphics = new GameGraphics();
        gameGraphics.start(primaryStage);

    }

    public static void main(String[] args) {
        launch(args);
    }

    public static boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public static Gamemode getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Gamemode currentGame) {
        this.currentGame = currentGame;
    }
}
