package org.example.fxgui;

import javafx.application.Application;
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

public class MainWindow extends Application {
    private static final String BACKGROUND_COLOR = "rgb(36, 43, 66)";
    private static final String BUTTON_DEFAULT_COLOR = "rgb(70, 103, 210)";
    private static final String BUTTON_HOVER_COLOR = "rgb(111, 137, 220)";
    private static final String BUTTON_ACTIVE_COLOR = "rgb(45, 77, 185)";

    static List<Boolean> seats = new ArrayList<>();
    private VBox seatLayout;
    private Button startButton;
    private String currentGame = "";

    @Override
    public void start(Stage primaryStage) {
        PiController piController = new PiController();
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

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Casino Spiele");
        primaryStage.setScene(scene);

        // Fenster maximieren und Minimalgröße setzen
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        primaryStage.show();
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

    private void handleStartGame() {
        System.out.println("Spiel " + currentGame + " wird gestartet!");
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
        currentGame = game;

        // Sitzplatzliste zurücksetzen und mit der neuen Anzahl initialisieren
        seats.clear();
        for (int i = 0; i < 6; i++) {
            seats.add(false);  // Alle Plätze sind zu Beginn leer
        }

        seatLayout.getChildren().clear();

        Text title = new Text(game + " - Sitzplätze");
        title.setStyle("-fx-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        // Container für die Sitze mit absolutem Positioning
        Pane seatsPane = new Pane();
        seatsPane.setPrefSize(700, 350);  // Größerer Bereich für den Halbkreis

        // Halbkreis-Parameter
        double centerX = 350;  // Mittelpunkt X
        double centerY = 300;  // Mittelpunkt Y (näher am unteren Rand)
        double radius = 250;   // Radius des Halbkreises

        // Berechne die Positionen für jeden Sitz
        for (int i = 0; i < seats.size(); i++) {
            // Berechne den Winkel für jeden Sitz (180 Grad verteilt)
            double angle = Math.PI * (i / (double)(seats.size() - 1));

            // Berechne die Position auf dem Halbkreis
            double x = centerX + radius * Math.cos(angle);
            double y = centerY - radius * Math.sin(angle);  // Minus, weil Y-Achse nach unten geht

            VBox seatContainer = new VBox(15);
            seatContainer.setAlignment(Pos.CENTER);

            Circle seat = new Circle(40);
            seat.setFill(seats.get(i) ? Color.RED : Color.GREEN);

            Text seatNumber = new Text("Platz " + (i + 1));
            seatNumber.setStyle("-fx-fill: white; -fx-font-size: 16px;");

            int finalI = i;
            seat.setOnMouseClicked(e -> {
                seats.set(finalI, !seats.get(finalI));
                seat.setFill(seats.get(finalI) ? Color.RED : Color.GREEN);
            });

            seatContainer.getChildren().addAll(seat, seatNumber);

            // Positioniere den Container
            seatContainer.setLayoutX(x - 40);  // Zentriere den Container
            seatContainer.setLayoutY(y - 40);

            seatsPane.getChildren().add(seatContainer);
        }

        // Füge einen transparenten Tisch in der Mitte hinzu
        Circle table = new Circle(centerX, centerY - 30, 100);
        table.setFill(Color.rgb(70, 103, 210, 0.3));  // Halbtransparentes Blau
        table.setStroke(Color.rgb(111, 137, 220, 0.5));
        table.setStrokeWidth(2);
        seatsPane.getChildren().add(0, table);  // Füge den Tisch hinter den Sitzen ein

        seatLayout.getChildren().addAll(title, seatsPane);
        seatLayout.setVisible(true);
        startButton.setVisible(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
