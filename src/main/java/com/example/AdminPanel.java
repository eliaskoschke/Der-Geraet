package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
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
        Button motorConfig = newStyledButton("Auswerfmotor Konfigurieren");
        Button esc = newStyledButton("Zurück zum Menü");

        VBox buttonBox = new VBox(20);
        buttonBox.getChildren().addAll(esc, karteAuswerfen, motorConfig, kicken, spielerMenu);
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

        VBox motorMenu = new VBox(5);
        motorMenu.setAlignment(Pos.CENTER);
        motorMenu.setPadding(new Insets(15));

        Text t1 = newStyledText("Geschwindigkeit Vorwärts");
        Text t2 = newStyledText("Dauer vorwärts");
        Text t3 = newStyledText("Dauer der Pause");
        Text t4 = newStyledText("Geschwindigkeit rückwärts");
        Text t5 = newStyledText("Dauer rückwärts");

        TextField vorspeed = newStyledTextField(String.valueOf(getDatabaseValuesByName("vorwaertsGeschwindigkeit")));
        vorspeed.setOnMouseClicked(e -> showNumberPad(vorspeed));

        TextField vordauer = newStyledTextField(String.valueOf(getDatabaseValuesByName("vorwaertsDauer")));
        vordauer.setOnMouseClicked(e -> showNumberPad(vordauer));

        TextField pause = newStyledTextField(String.valueOf(getDatabaseValuesByName("pauseDauer")));
        pause.setOnMouseClicked(e -> showNumberPad(pause));

        TextField rueckspeed = newStyledTextField(String.valueOf(getDatabaseValuesByName("rueckwaertsGeschwindigkeit")));
        rueckspeed.setOnMouseClicked(e -> showNumberPad(rueckspeed));

        TextField rueckdauer = newStyledTextField(String.valueOf(getDatabaseValuesByName("rueckwaertsDauer")));
        rueckdauer.setOnMouseClicked(e -> showNumberPad(rueckdauer));

        Button speichern = newStyledButton("Speichern");
        Button standart = newStyledButton("Standartwerte");
        motorMenu.getChildren().addAll(t1, vorspeed, t2, vordauer, t3, pause, t4, rueckspeed, t5, rueckdauer, standart, speichern);

        spielerBox.getChildren().add(drehen);

        drehen.setOnAction(e -> rotateStepper(0));

        karteAuswerfen.setOnAction(e -> giveCard());

        kicken.setOnAction(e -> kickAllPlayers());

        speichern.setOnAction(e -> {
            sendControllerDatabaseUpdate("vorwaertsGeschwindigkeit", Integer.parseInt(vorspeed.getText()));
            sendControllerDatabaseUpdate("vorwaertsDauer", Integer.parseInt(vordauer.getText()));
            sendControllerDatabaseUpdate("pauseDauer", Integer.parseInt(pause.getText()));
            sendControllerDatabaseUpdate("rueckwaertsGeschwindigkeit", Integer.parseInt(rueckspeed.getText()));
            sendControllerDatabaseUpdate("rueckwaertsDauer", Integer.parseInt(rueckdauer.getText()));
        });

        standart.setOnAction(e -> {
            List<Integer> values = getDatabseDefaultValues();

            vorspeed.setText(String.valueOf(values.get(0)));
            vordauer.setText(String.valueOf(values.get(1)));
            pause.setText(String.valueOf(values.get(2)));
            rueckspeed.setText(String.valueOf(values.get(3)));
            rueckdauer.setText(String.valueOf(values.get(4)));
        });
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

        motorConfig.setOnAction(e -> {
            if (root.getChildren().contains(buttonBox)) {
                root.getChildren().removeAll();

                buttonBox.getChildren().remove(motorConfig);
                motorMenu.getChildren().add(motorConfig);
                motorConfig.setText("Zurück");

                root.setTop(motorMenu);
            } else {
                root.getChildren().removeAll();

                buttonBox.getChildren().removeAll(kicken, spielerMenu);
                motorMenu.getChildren().remove(motorConfig);
                buttonBox.getChildren().addAll(motorConfig, kicken, spielerMenu);
                motorConfig.setText("Auswerfmotor Konfigurieren");

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
            primaryStage.setFullScreen(true);
        });
    }

    private void showNumberPad(TextField textField) {
        Popup popup = new Popup();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        for (int i = 0; i < 10; i++) {
            Button button = newStyledButton(String.valueOf(i));
            button.setPrefSize(60, 60); // Größe der Buttons anpassen
            button.setOnAction(e -> {
                textField.setText(textField.getText() + button.getText());
            });
            gridPane.add(button, i % 3, i / 3);
        }

        // Hinzufügen des "<-" Buttons
        Button backspaceButton = newStyledButton("<-");
        backspaceButton.setPrefSize(60, 60);
        backspaceButton.setOnAction(e -> {
            String currentText = textField.getText();
            if (!currentText.isEmpty()) {
                textField.setText(currentText.substring(0, currentText.length() - 1));
            }
        });
        gridPane.add(backspaceButton, 2, 3); // Ändere die Positionierung des Buttons im GridPane

        VBox vBox = new VBox(gridPane);
        popup.getContent().add(vBox);
        popup.setAutoHide(false); // Setze AutoHide auf false, damit es nicht automatisch geschlossen wird
        popup.show(textField.getScene().getWindow(), textField.getLayoutX() + textField.getWidth() - 150, 250);

        // Füge einen Listener hinzu, um das Popup zu schließen, wenn ein anderes Feld fokussiert wird
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                popup.hide();
            }
        });
    }

    private static List<Integer> getDatabseDefaultValues() {
        List<Integer> valueList = new ArrayList<>();
        String[] namen = {"standartVorwaertsGeschwindigkeit", "standartVorwaertsDauer",
                "standartPauseDauer", "standartRueckwaertsGeschwindigkeit", "StandartRueckwaertsDauer"};
        for (int i = 0; i < namen.length; i++) {

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String message = "{\"message\":\"" + namen[i] + "\"}";

                HttpPost postRequest = new HttpPost(baseURL + "/admin/adminPanel/getDatabaseValuesByName");
                postRequest.setHeader("Content-Type", "application/json");

                postRequest.setEntity(new StringEntity(message));
                System.out.println("Button wurde geklickt");
                try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                    Message responseMessage = mapper.readValue(EntityUtils.toString(response.getEntity()), Message.class);
                    valueList.add(Integer.parseInt(responseMessage.getMessage()));
                }
            } catch (Exception exception) {
                exception.getStackTrace();
            }
        }
        return valueList;
    }

    private static int getDatabaseValuesByName(String name){
        int value = 0;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String message = "{\"message\":\"" + name + "\"}";

            HttpPost postRequest = new HttpPost(baseURL + "/admin/adminPanel/getDatabaseValuesByName");
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(message));
            System.out.println("Button wurde geklickt");
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                Message responseMessage = mapper.readValue(EntityUtils.toString(response.getEntity()), Message.class);
                value = Integer.parseInt(responseMessage.getMessage());
            }
        } catch (Exception exception) {
            exception.getStackTrace();
        }
        return value;
    }

    private static void sendControllerDatabaseUpdate(String name, int wert) {
        String postMessage = name+"-"+String.valueOf(wert);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String message = "{\"message\":\"" + postMessage + "\"}";

            HttpPost postRequest = new HttpPost(baseURL + "/admin/adminPanel/saveUpdateInDatabase");
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(message));
            System.out.println("Button wurde geklickt");
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                Message responseMessage = mapper.readValue(EntityUtils.toString(response.getEntity()), Message.class);

            }
        } catch (Exception exception) {
            exception.getStackTrace();
        }
    }

    private static void rotateStepper(int id){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String message = "{\"message\":\"" + id + "\"}";

            HttpPost postRequest = new HttpPost(baseURL + "/admin/adminPanel/rotateStepper");
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(message));
            System.out.println("Button wurde geklickt");
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                Message responseMessage = mapper.readValue(EntityUtils.toString(response.getEntity()), Message.class);

            }
        } catch (Exception exception) {
            exception.getStackTrace();
        }
    }

    private static void giveCard() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String message = "{\"message\":\"\"}";

            HttpPost postRequest = new HttpPost(baseURL + "/admin/adminPanel/activateCardMotor");
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(message));
            System.out.println("Button wurde geklickt");
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                Message responseMessage = mapper.readValue(EntityUtils.toString(response.getEntity()), Message.class);

            }
        } catch (Exception exception) {
            exception.getStackTrace();
        }
    }

    private static void kickAllPlayers() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String message = "{\"message\":\"\"}";

            HttpPost postRequest = new HttpPost(baseURL + "/admin/sendReset");
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(message));
            System.out.println("Button wurde geklickt");
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
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
                    -fx-font-size: 19px;
                    -fx-font-weight: Bold;
                    -fx-max-height: 275px;
                    -fx-max-width: 350px;
                    -fx-height: 100%;
                    -fx-width: 100%;
                    -fx-background-radius: 30;
                    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);
                """;

        button.setStyle(css);
        return button;
    }

    public static TextField newStyledTextField(String text) {
        TextField textField = new TextField();

        textField.setText(text);
        String css = """
                -fx-background-color: rgb(70, 103, 210);
                -fx-text-fill: white;
                -fx-font-size: 20px;
                -fx-font-weight: Bold;
                -fx-max-height: 275px;
                -fx-max-width: 350px;
                -fx-height: 100%;
                -fx-width: 100%;
                -fx-background-radius: 30;
                """;

        textField.setStyle(css);
        textField.setPromptText("Neuen Wert Eingeben");
        return textField;
    }

    public static Text newStyledText(String text) {
        Text text1 = new Text(text);

        String css = """
                -fx-font-size: 20px;
                -fx-font-weight: Bold;
                """;
        text1.setStyle(css);
        text1.setFill(Color.WHITE);
        return text1;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
