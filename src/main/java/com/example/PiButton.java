package com.example;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class PiButton {
    private static final String baseURL = "http://localhost:8080/api/logic";
    private static final long DOUBLE_CLICK_TIME = 300; // Zeit in Millisekunden
    private long lastPressTime = 0;
    private boolean isDoubleClick = false;
    private int pinNumber = 0;
    private boolean buttonRegistered = false;
    private final int playerNumber;

    public PiButton(com. pi4j. context. Context pi4j, int pinNumber) throws InterruptedException {
        this.pinNumber = pinNumber;
        this.playerNumber = convertPinNumberToPlayerNumber();
        var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                .name("Button Name: " + String.valueOf(pinNumber))
                .id("Button ID: " + String.valueOf(pinNumber))
                .address(pinNumber)
                .pull(PullResistance.PULL_DOWN)
                .debounce(150L);

        var button = pi4j.create(buttonConfig);

        button.addListener(e -> {
            if (e.state() == DigitalState.HIGH) {
                handleClick();
            }
        });
    }

    public int convertPinNumberToPlayerNumber(){
        return switch (pinNumber) {
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 3;
            case 14 -> 4;
            case 17 -> 5;
            case 27 -> 6;
            default -> 0;
        };
    }



    public void handleClick() {
        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastPressTime) < DOUBLE_CLICK_TIME) {
            isDoubleClick = true;
            System.out.println("Doppelklick erkannt!");
            doubleButtonClick();
        } else {
            isDoubleClick = false;
            lastPressTime = currentTime;
        }
    }

    public void checkSingleClick() {
        if (!isDoubleClick && (System.currentTimeMillis() - lastPressTime) >= DOUBLE_CLICK_TIME && lastPressTime != 0) {
            System.out.println("Einfacher Klick erkannt!");
            singleButtonClick();
            lastPressTime = 0; // Zurücksetzen, um zukünftige Klicks korrekt zu erkennen
        }
    }

    private void singleButtonClick() {
        System.out.println("Knopf gedrückt");
        if(buttonRegistered) {
            sendMessageButtonClickedOnce();
        } else{
            resgiterPlayerAtTable();
        }
    }

    private void resgiterPlayerAtTable() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String message = "{\"message\":\"" + playerNumber + "\"}";

            HttpPost postRequest = new HttpPost(baseURL + "/registerPlayerAtTable");
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(message));

            // Sende die POST-Anfrage und erhalte die Antwort
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                // Überprüfe den Status der Antwort und verarbeite sie
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println("Antwort erhalten: " + responseBody);
                    buttonRegistered = false;
                } else {
                    System.err.println("Fehler: " + statusCode);
                }
            }
        } catch (Exception exception) {
            exception.getStackTrace();
        }
    }

    private void sendMessageButtonClickedOnce() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String message = "{\"message\":\"Button clicked: " + playerNumber + "\"}";

            HttpPost postRequest = new HttpPost(baseURL + "/buttonIsClickedOnce");
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(message));

            // Sende die POST-Anfrage und erhalte die Antwort
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                // Überprüfe den Status der Antwort und verarbeite sie
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println("Antwort erhalten: " + responseBody);
                } else {
                    System.err.println("Fehler: " + statusCode);
                }
            }
        } catch (Exception exception) {
            exception.getStackTrace();
        }
    }

    private void doubleButtonClick() {
        System.out.println("Knopf gedrückt");
        if(buttonRegistered) {
            sendMessageButtonClickedTwice();
        } else{
            resgiterPlayerAtTable();
        }
    }

    private void sendMessageButtonClickedTwice() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String message = "{\"message\":\"Button clicked: "+ playerNumber+"\"}";

            HttpPost postRequest = new HttpPost(baseURL + "/buttonIsClickedTwice");
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(message));

            // Sende die POST-Anfrage und erhalte die Antwort
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                // Überprüfe den Status der Antwort und verarbeite sie
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println("Antwort erhalten: " + responseBody);
                } else {
                    System.err.println("Fehler: " + statusCode);
                }
            }
        } catch (Exception exception){
            exception.getStackTrace();
        }
    }
}
