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
    private static final String BUTTON_ACTIVE_COLOR = "rgb(255, 255, 255)";

    static List<Boolean> seats = new ArrayList<>(6);
    static List<Circle> circles = new ArrayList<>(6);
    private static VBox seatLayout;
    private Button startButton;
    private static Gamemode currentGame;
    private static boolean gameStarted = false;
    private static List<String> playerList = new ArrayList<>();

    public GameFX() {
        seats = new ArrayList<>(6);
        circles = new ArrayList<>(6);
        VBox seatLayout;
        gameStarted = false;
    }

    @Override
    public void start(Stage primaryStage) {
        // Hauptlayout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        root.setPadding(new Insets(20));

        // Buttons erstellen
        Button blackJackBtn = createStyledButton("Black Jack");
        Button pokerBtn = createStyledButton("Poker");
        Button luegeBtn = createStyledButton("Lüge");
        Button adminPanelBtn = createStyledButton("Admin Dialog");
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
        seatLayout.setAlignment(Pos.TOP_CENTER); // Zentriere den Inhalt
        seatLayout.setVisible(false);
        root.setCenter(seatLayout);

        // Start Button im unteren Bereich
        startButton = createStyledButton("Start");
        addHoverEffect(startButton);
        startButton.setVisible(false);


        VBox bottomBox = new VBox(20);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));
        bottomBox.getChildren().addAll(startButton);

        VBox adminBox = new VBox(20);
        adminBox.setAlignment(Pos.BOTTOM_RIGHT);
        adminBox.getChildren().add(adminPanelBtn);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomContainer = new HBox(60);
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.getChildren().addAll(spacer, bottomBox, adminBox);
        HBox.setHgrow(bottomBox, Priority.ALWAYS);
        root.setBottom(bottomContainer);

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

        adminPanelBtn.setOnAction(e -> {
            AdminPanel adminPanel = new AdminPanel();
            try {
                adminPanel.start(primaryStage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        startButton.setOnAction(e -> {
            gameStarted = true;
            GameGraphics gameGraphics = new GameGraphics(false);
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
//            primaryStage.setWidth(1920);
//            primaryStage.setHeight(1185);
            primaryStage.setFullScreen(true);
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
                    -fx-font-size: 30px; // Schriftgröße erhöht
                    -fx-font-weight: Bold;
                    -fx-min-width: 150px; // Breite erhöht
                    -fx-min-height: 80px; // Höhe erhöht
                    -fx-background-radius: 30;
                    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);
                """, BUTTON_DEFAULT_COLOR);
    }//TEST

    private String getHoverButtonStyle() {
        return String.format("""
                    -fx-background-color: %s;
                    -fx-text-fill: white;
                    -fx-font-size: 30px; // Schriftgröße erhöht
                    -fx-font-weight: Bold;
                    -fx-min-width: 150px; // Breite erhöht
                    -fx-min-height: 80px; // Höhe erhöht
                    -fx-background-radius: 30;
                    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);
                """, BUTTON_HOVER_COLOR);
    }

    private String getActiveButtonStyle() {
        return String.format("""
                    -fx-background-color: %s;
                    -fx-text-fill: black;
                    -fx-font-size: 30px; // Schriftgröße erhöht
                    -fx-font-weight: Bold;
                    -fx-min-width: 150px; // Breite erhöht
                    -fx-min-height: 80px; // Höhe erhöht
                    -fx-background-radius: 30;
                    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);
                """, BUTTON_ACTIVE_COLOR);
    }

    private void toggleButtonStyle(Button button) {
        if (!button.getStyle().contains(BUTTON_ACTIVE_COLOR)) {
            button.setStyle(getActiveButtonStyle());
        }
    }

    private void showSeats(String game) {
        switch (game) {
            case "Black Jack" -> {
                currentGame = Gamemode.BLACKJACK;
            }
            case "Poker" -> {
                currentGame = Gamemode.POKER;
            }
        }

        // Reset and initialize seat list
        seats.clear();
        for (int i = 0; i < 6; i++) {
            seats.add(false); // All seats start as empty
        }

        seatLayout.getChildren().clear();

        Text title = new Text(game + " - Sitzplätze");
        title.setStyle("-fx-fill: white; -fx-font-size: 40px; -fx-font-weight: bold;");
        title.setTranslateY(20); // Hebe den Titel an

        // Seat container
        Pane seatsPane = new Pane();
        seatsPane.setPrefSize(1000, 400);

        double centerX = 490;
        double centerY = 80; // Verschiebe die Kreise weiter nach unten
        double radius = 130; // Radius erhöht, um die Kreise weiter zu entfernen

        for (int i = 0; i < seats.size(); i++) {
            double angle = Math.PI * (1 - i / (double) (seats.size() - 1));

            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            VBox seatContainer = new VBox(8);
            seatContainer.setAlignment(Pos.CENTER);

            Circle seat = new Circle(25); // Radius der Kreise
            seat.setFill(seats.get(i) ? Color.RED : Color.GREEN);
            circles.add(seat);
            Text seatNumber = new Text("Platz " + (i + 1));
            seatNumber.setStyle("-fx-fill: white; -fx-font-size: 15px; -fx-font-weight: Bold;");

            int finalI = i;
            seat.setOnMouseClicked(e -> {
                seats.set(finalI, !seats.get(finalI));
                seat.setFill(seats.get(finalI) ? Color.RED : Color.GREEN);
                if(playerList.contains(String.valueOf(finalI+1))){
                    seat.setFill(Color.RED);
                } else
                    seat.setFill(Color.GREEN);
            });

            seatContainer.getChildren().addAll(seat, seatNumber);

            // **Perfectly center seat containers**
            seatContainer.setLayoutX(x - seat.getRadius());
            seatContainer.setLayoutY(y - seat.getRadius() + 20); // Text unter den Kreisen

            seatsPane.getChildren().add(seatContainer);
        }

        // Adjusted table position & size
        Circle table = new Circle(centerX, centerY + 25, 80); // Größerer Tisch
        table.setFill(Color.rgb(70, 103, 210, 0.3));
        table.setStroke(Color.rgb(111, 137, 220, 0.5));
        table.setStrokeWidth(2);
        seatsPane.getChildren().add(0, table);

        // Arrange everything
        seatLayout.getChildren().addAll(title, seatsPane);
        seatLayout.setVisible(true);
        startButton.setVisible(true);
    }

    public static void updateSeatColor(List<String> id) {
        for (int i = 0; i < circles.size(); i++) {
            if(i<id.size()) {
                int seat = Integer.parseInt(id.get(i)) - 1;
                circles.get(seat).setFill(Color.RED);
            } else{
                if(!id.contains(String.valueOf(i+1)))
                    circles.get(i).setFill(Color.GREEN);
            }
        }
        playerList =  id;
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
        GameGraphics gameGraphics = new GameGraphics(false);
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