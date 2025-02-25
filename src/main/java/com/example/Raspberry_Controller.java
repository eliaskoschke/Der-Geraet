package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import static com.pi4j.Pi4J.newAutoContext;

public class Raspberry_Controller {
    static Context pi4j = newAutoContext();
    private static String baseURL = "http://localhost:8080/api/logic";
    private static ObjectMapper mapper = new ObjectMapper();
    static HashMap<String, DigitalOutput> playerButtonMap = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

//        int buttonNumber = 22;
//
//        var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
//                .name("Button")
//                .id("Button " + String.valueOf(buttonNumber))
//                .address(buttonNumber)
//                .pull(PullResistance.PULL_DOWN)
//                .debounce(1000L);
//
//        var button = pi4j.create(buttonConfig);
//
//        button.addListener(e -> {
//           if (e.state() == DigitalState.HIGH) {
//               System.out.println("Knopf gedrückt");
//                int thisButtonNumber = buttonNumber;
//               try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//                   String message = "{\"message\":\"Button clicked: "+ thisButtonNumber+"\"}";
//
//                   HttpPost postRequest = new HttpPost(baseURL + "/buttonIsClicked");
//                   postRequest.setHeader("Content-Type", "application/json");
//
//                   postRequest.setEntity(new StringEntity(message));
//
//                   // Sende die POST-Anfrage und erhalte die Antwort
//                   try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
//                       // Überprüfe den Status der Antwort und verarbeite sie
//                       int statusCode = response.getStatusLine().getStatusCode();
//                       if (statusCode == 200) {
//                           String responseBody = EntityUtils.toString(response.getEntity());
//                           System.out.println("Antwort erhalten: " + responseBody);
//                       } else {
//                           System.err.println("Fehler: " + statusCode);
//                       }
//                   }
//               } catch (Exception exception){
//                   exception.getStackTrace();
//               }
//           }
//        });
        //PiButton button = new PiButton(pi4j, 15);
        addButtonOutputs("1", MappingForButtonIds.getLEDPinAdressForPlayerID("1"));
        addButtonOutputs("2", MappingForButtonIds.getLEDPinAdressForPlayerID("2"));
        addButtonOutputs("3", MappingForButtonIds.getLEDPinAdressForPlayerID("3"));
        addButtonOutputs("4", MappingForButtonIds.getLEDPinAdressForPlayerID("4"));
        addButtonOutputs("5", MappingForButtonIds.getLEDPinAdressForPlayerID("5"));
        addButtonOutputs("6", MappingForButtonIds.getLEDPinAdressForPlayerID("6"));

        ArrayList<PiButton> buttonList = new ArrayList<>();
        buttonList.add(new PiButton(pi4j, MappingForButtonIds.getButtonPinAdressForPlayerID("1")));
        buttonList.add(new PiButton(pi4j, MappingForButtonIds.getButtonPinAdressForPlayerID("2")));
        buttonList.add(new PiButton(pi4j, MappingForButtonIds.getButtonPinAdressForPlayerID("3")));
        buttonList.add(new PiButton(pi4j, MappingForButtonIds.getButtonPinAdressForPlayerID("4")));
        buttonList.add(new PiButton(pi4j, MappingForButtonIds.getButtonPinAdressForPlayerID("5")));
        buttonList.add(new PiButton(pi4j, MappingForButtonIds.getButtonPinAdressForPlayerID("6")));

        while(true){
            if(gameHasStarted()) {
                String currentPlayerId = getCurrentPLayerId();
                for (PiButton piButton : buttonList) {
                    piButton.checkSingleClick();
                    if (currentPlayerId.equals(String.valueOf(piButton.getPlayerNumber()))) {
                        activateButton(piButton);
                    } else if (playerButtonMap.get(String.valueOf(piButton.getPlayerNumber())).isHigh()) {
                        deactivateButton(piButton);
                    }
                }
                Thread.sleep(50);
            } else {
                manageButtonsForRegistration(buttonList);
            }
        }

    }

    private static void manageButtonsForRegistration(ArrayList<PiButton> buttonList) {
        for (PiButton piButton : buttonList) {
            piButton.checkSingleClick();
            if(playerButtonMap.get(String.valueOf(piButton.getPlayerNumber())).isLow() && !piButton.isButtonRegistered()){
                activateButton(piButton);
            }
            if(piButton.isButtonRegistered() && playerButtonMap.get(String.valueOf(playerButtonMap)).isHigh()){
                deactivateButton(piButton);
            }
        }
    }

    public static String getCurrentPLayerId() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String playerID ="";
            // Erstelle die URL ohne zusätzliche Parameter
            String url = baseURL + "/ping/getPlayerTurn";

            // Erstelle die GET-Anfrage
            HttpGet getRequest = new HttpGet(url);
            getRequest.setHeader("Content-Type", "application/json");

            // Sende die GET-Anfrage und erhalte die Antwort
            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                // Überprüfe den Status der Antwort und verarbeite sie
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println("Antwort erhalten: " + responseBody);
                    Message responseMessage = mapper.readValue(EntityUtils.toString(response.getEntity()), Message.class);
                    System.out.println(responseMessage);
                    playerID = responseMessage.getMessage();
                    return playerID;
                } else {
                    System.err.println("Fehler: " + statusCode);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "0";
    }

    private static void addButtonOutputs(String playerID, int adresse) {
        var buttonOutput = pi4j.create(DigitalOutput.newConfigBuilder(pi4j)
                .name("Freigabe für Player "+ playerID)
                .id("Freigabe "+String.valueOf(adresse))
                .address(adresse) //passende adresse einfügen
                .initial(DigitalState.LOW)
                .onState(DigitalState.HIGH));

        playerButtonMap.put(playerID, buttonOutput);
    }

    private static void activateButton(PiButton piButton){
        piButton.setLocked(true);
        playerButtonMap.get(String.valueOf(piButton.getPlayerNumber())).high();
        piButton.setLocked(false);
    }

    private static void deactivateButton(PiButton piButton){
        piButton.setLocked(true);
        playerButtonMap.get(String.valueOf(piButton.getPlayerNumber())).low();
    }

    public static boolean gameHasStarted() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Erstelle die URL ohne zusätzliche Parameter
            String url = baseURL + "/ping";

            // Erstelle die GET-Anfrage
            HttpGet getRequest = new HttpGet(url);
            getRequest.setHeader("Content-Type", "application/json");

            // Sende die GET-Anfrage und erhalte die Antwort
            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                // Überprüfe den Status der Antwort und verarbeite sie
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println("Antwort erhalten: " + responseBody);
                    Message responseMessage = mapper.readValue(EntityUtils.toString(response.getEntity()), Message.class);
                    System.out.println(responseMessage);
                    if(responseMessage.getMessage().equals("Game has started")){
                        return true;
                    }
                } else {
                    System.err.println("Fehler: " + statusCode);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }
}
