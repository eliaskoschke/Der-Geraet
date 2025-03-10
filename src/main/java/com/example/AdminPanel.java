package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;


public class AdminPanel extends Application {
    private static final String BACKGROUND_COLOR = "rgb(36, 43, 66)";
    private static ObjectMapper mapper = new ObjectMapper();
    private static String baseURL = "http://localhost:8080/api";

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        Button drehen = newStyledButton("Zum Ursprung");
        Button karteAuswerfen = newStyledButton("Karte Auswerfen");
        Button kicken = newStyledButton("Alle Spieler Kicken");
        Button spielerMenu = newStyledButton("Dreh Menü");
        Button esc = newStyledButton("Zurück zum Menü");

        VBox buttonBox = new VBox(20);
        buttonBox.getChildren().addAll(esc, karteAuswerfen, kicken, spielerMenu);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(150));
        root.setTop(buttonBox);

        List<Button> spieler = new ArrayList<>();
        TilePane spielerBox = new TilePane(20, 20);
        spielerBox.setAlignment(Pos.CENTER);
        spielerBox.setPadding(new Insets(150));

        for (int i = 0; i < 6; i++) {
            Button button = newStyledButton("Spieler " + String.valueOf(i + 1));
            int finalI = i;
            button.setOnAction(e -> {rotateStepper(finalI +1);});
            spieler.add(button);
            spielerBox.getChildren().add(button);
        }

        spielerBox.getChildren().add(drehen);

        drehen.setOnAction(e -> rotateStepper(0));

        karteAuswerfen.setOnAction(e -> {giveCard();});

        kicken.setOnAction(e -> {kickAllPlayers();});

        esc.setOnAction(e -> {
            GameFX gameFX = new GameFX();
            gameFX.start(primaryStage);
        });

        spielerMenu.setOnAction(e -> {
            if (root.getChildren().contains(buttonBox)) {
                root.getChildren().removeAll();

                buttonBox.getChildren().remove(spielerMenu);
                spielerBox.getChildren().add(spielerMenu);
                spielerMenu.setText("Zurück");

                root.setTop(spielerBox);
            } else {
                root.getChildren().removeAll();

                spielerBox.getChildren().remove(spielerMenu);
                buttonBox.getChildren().add(spielerMenu);
                spielerMenu.setText("Dreh Menü");
                root.setTop(buttonBox);
            }
        });

        Scene scene = new Scene(root, 1024,600);
        primaryStage.setTitle("Admin Dialog");
        primaryStage.setScene(scene);

        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        Platform.runLater(() -> {
//            primaryStage.setWidth(1024);
//            primaryStage.setHeight(600);
            primaryStage.setFullScreen(true);
        });
    }
    private static void rotateStepper(int id){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //Todo: Warum hast du das so gemacht? Mach doch einfach ein Message Objekt du Idiot
            String message = "{\"message\":\"" + id + "\"}";

            HttpPost postRequest = new HttpPost(baseURL + "/admin/adminPanel/rotateStepper");
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(message));
            System.out.println("Button wurde geklickt");
            // Sende die POST-Anfrage und erhalte die Antwort
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                // Überprüfe den Status der Antwort und verarbeite sie
                Message responseMessage = mapper.readValue(EntityUtils.toString(response.getEntity()), Message.class);

            }
        } catch (Exception exception) {
            exception.getStackTrace();
        }
    }

    private static void giveCard() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //Todo: Warum hast du das so gemacht? Mach doch einfach ein Message Objekt du Idiot
            String message = "{\"message\":\"\"}";

            HttpPost postRequest = new HttpPost(baseURL + "/admin/adminPanel/activateCardMotor");
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(message));
            System.out.println("Button wurde geklickt");
            // Sende die POST-Anfrage und erhalte die Antwort
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                // Überprüfe den Status der Antwort und verarbeite sie
                Message responseMessage = mapper.readValue(EntityUtils.toString(response.getEntity()), Message.class);

            }
        } catch (Exception exception) {
            exception.getStackTrace();
        }
    }

    private static void kickAllPlayers() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //Todo: Warum hast du das so gemacht? Mach doch einfach ein Message Objekt du Idiot
            String message = "{\"message\":\"\"}";

            HttpPost postRequest = new HttpPost(baseURL + "/admin/sendReset");
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(message));
            System.out.println("Button wurde geklickt");
            // Sende die POST-Anfrage und erhalte die Antwort
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                // Überprüfe den Status der Antwort und verarbeite sie
                Message responseMessage = mapper.readValue(EntityUtils.toString(response.getEntity()), Message.class);

            }
        } catch (Exception exception) {
            exception.getStackTrace();
        }
    }

    public static Button newStyledButton(String text) {
        Button button = new Button(text);
        String css = """
                    -fx-background-color: rgb(70, 103, 210);
                    -fx-text-fill: white;
                    -fx-font-size: 25px;
                    -fx-font-weight: Bold;
                    -fx-max-height: 275px;
                    -fx-max-width: 300px;
                    -fx-height: 100%;
                    -fx-width: 100%;
                    -fx-background-radius: 30;
                    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);
                """;

        button.setStyle(css);
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
