package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final long DOUBLE_CLICK_TIME = 1000; // Zeit in Millisekunden das ist ein test2
    private long lastPressTime = 0;
    private boolean isDoubleClick = false;
    private int pinNumber = 0;
    private boolean buttonRegistered = false;
    private final int playerNumber;
    private ObjectMapper mapper = new ObjectMapper();
    private boolean locked = true;
    private static boolean isWaitingForSecondClick = false;

    public PiButton(com. pi4j. context. Context pi4j, String playerNumber) throws InterruptedException {
        this.pinNumber = MappingForAdress.getButtonPinAdressForPlayerID(playerNumber);
        this.playerNumber = Integer.parseInt(playerNumber);
        var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                .name("Button Name: " + String.valueOf(pinNumber))
                .id("Button ID: " + String.valueOf(pinNumber))
                .address(pinNumber)
                .pull(PullResistance.PULL_DOWN)
                .debounce(3000L);

        var button = pi4j.create(buttonConfig);

        button.addListener(e -> {
            if (e.state() == DigitalState.HIGH) {
                if(!buttonRegistered){
                    singleButtonClick();
                } else {
                    if (!locked) {
                        long currentTime = System.currentTimeMillis();
                        if(isWaitingForSecondClick && (currentTime - lastPressTime) < DOUBLE_CLICK_TIME){
                            doubleButtonClick();
                            System.out.println("DOPPELKLICK");
                            isWaitingForSecondClick = false;
                        } else{
                            lastPressTime = currentTime;
                            isWaitingForSecondClick = true;
                            new java.util.Timer().schedule(new java.util.TimerTask(){
                                @Override
                                public void run(){
                                    if(isWaitingForSecondClick) {
                                        singleButtonClick();
                                        System.out.println("Einzelklick");
                                        isWaitingForSecondClick = false;
                                    }
                                }
                            }, DOUBLE_CLICK_TIME);

                        }
                    }
                }
//                System.out.println("Button "+playerNumber +" wurde geklickt");
            }
        });
    }




    private void singleButtonClick() {
        if(!locked) {
            if (buttonRegistered) {
                sendMessageButtonClickedOnce();
            } else {
                System.out.println("Ich bin im Einzel klick und regestriere gleich");
                resgiterPlayerAtTable();
            }
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
                Message responseMessage = mapper.readValue(EntityUtils.toString(response.getEntity()), Message.class);
                if (responseMessage.getMessage().equals("acknowledged")) {
                    buttonRegistered = true;
                } else {
                    System.out.println("Fehler: ");
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
        if(!locked) {
            if (buttonRegistered) {
                sendMessageButtonClickedTwice();
            } else {
                System.out.println("Ich bin im Doppelbutton klick und regestriere gleich");
                resgiterPlayerAtTable();
            }
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


    public int getPlayerNumber() {
        return playerNumber;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isButtonRegistered() {
        return buttonRegistered;
    }

    public void setButtonRegistered(boolean buttonRegistered) {
        this.buttonRegistered = buttonRegistered;
    }
}
