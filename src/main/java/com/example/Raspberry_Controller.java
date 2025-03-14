package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static com.pi4j.Pi4J.newAutoContext;

public class   Raspberry_Controller {
    public static Context pi4j;
    private static String baseURL = "http://localhost:8080/api/logic";
    private static ObjectMapper mapper = new ObjectMapper();
    static HashMap<String, DigitalOutput> playerButtonMap = new HashMap<>();
    static boolean isRegistering = true;
    static boolean gameHasAlreadyStartedOnce = false;
    //TODO: Umbennenen in Tisch Client

    public Raspberry_Controller(Context pi4j){
        this.pi4j = pi4j;
    }

    public void execute() throws InterruptedException {
        //PiButton button = new PiButton(pi4j, 15);
        //Todo: for schleife einbauen
        addButtonOutputs("1", MappingForAdress.getLEDPinAdressForPlayerID("1"));
        addButtonOutputs("2", MappingForAdress.getLEDPinAdressForPlayerID("2"));
        addButtonOutputs("3", MappingForAdress.getLEDPinAdressForPlayerID("3"));
        addButtonOutputs("4", MappingForAdress.getLEDPinAdressForPlayerID("4"));
        addButtonOutputs("5", MappingForAdress.getLEDPinAdressForPlayerID("5"));
        addButtonOutputs("6", MappingForAdress.getLEDPinAdressForPlayerID("6"));

        //Todo: PiButton umbennenen in ButtonHandler
        ArrayList<PiButton> buttonList = new ArrayList<>();
        buttonList.add(new PiButton(pi4j, "1"));
        buttonList.add(new PiButton(pi4j, "2"));
        buttonList.add(new PiButton(pi4j, "3"));
        buttonList.add(new PiButton(pi4j, "4"));
        buttonList.add(new PiButton(pi4j, "5"));
        buttonList.add(new PiButton(pi4j, "6"));
        Thread.sleep(5000);
        while(true){
            if(!isRegistering) {
                gameHasAlreadyStartedOnce = true;
                String currentPlayerId = getCurrentPLayerId();
                for (PiButton piButton : buttonList) {
                    if (currentPlayerId.equals(String.valueOf(piButton.getPlayerNumber()))) {
                        piButton.setButtonRegistered(true);
                        if(playerButtonMap.get(currentPlayerId).isLow())
                            activateButton(piButton);

                    } else if (playerButtonMap.get(String.valueOf(piButton.getPlayerNumber())).isHigh()) {
                        deactivateButton(piButton);
                    }
                    Thread.sleep(50);
                }
            } else {
                if(gameHasAlreadyStartedOnce){
                    gameHasAlreadyStartedOnce = false;
                    deactivateAllNonRegisteredButtons(buttonList);
                    for(PiButton button : buttonList){

                        button.resetButton();
                        System.out.println("ALLE BUTTONS WURDE RESETED!!!!!!!!!!!!!!!!!!!!");
                    }
                }
                manageButtonsForRegistration(buttonList);
                if(!isRegistering) {
                    deactivateAllNonRegisteredButtons(buttonList);
                }
            }
            isRegistering = !gameHasStarted();
            Thread.sleep(100);
        }

    }


    private static void deactivateAllNonRegisteredButtons(ArrayList<PiButton> buttonList){
        for(PiButton piButton : buttonList){
            deactivateButton(piButton);
        }
    }

    private static void manageButtonsForRegistration(ArrayList<PiButton> buttonList) throws InterruptedException {
        for (PiButton piButton : buttonList) {
            if(playerButtonMap.get(String.valueOf(piButton.getPlayerNumber())).isLow() && !piButton.isButtonRegistered()){
                activateButton(piButton);
            }
            if(piButton.isButtonRegistered() && playerButtonMap.get(String.valueOf(piButton.getPlayerNumber())).isHigh()){
                deactivateButton(piButton);
            }
            //piButton.checkSingleClick();
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
                    Message responseMessage = mapper.readValue(responseBody, Message.class);
                    playerID = responseMessage.getMessage();
                    if(playerID == null){
                        playerID = "0";
                    }
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
        System.out.println(adresse);
        var buttonOutput = pi4j.create(DigitalOutput.newConfigBuilder(pi4j)
                .name("Freigabe für Player "+ playerID)
                .id("Freigabe "+String.valueOf(adresse))
                .address(adresse) //passende adresse einfügen
                .initial(DigitalState.LOW)
                .onState(DigitalState.HIGH));

        playerButtonMap.put(playerID, buttonOutput);
    }

    private static void activateButton(PiButton piButton) throws InterruptedException {
        piButton.setLocked(true);
        playerButtonMap.get(String.valueOf(piButton.getPlayerNumber())).high();
        Thread.sleep(100);
        piButton.setLocked(false);
    }

    private static void deactivateButton(PiButton piButton){
        //Todo: teste ob locked notwendig ist
        playerButtonMap.get(String.valueOf(piButton.getPlayerNumber())).low();
    }

    //TODO: Eigene Varibale setzen
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
                    Message responseMessage = mapper.readValue(responseBody, Message.class);
                    System.out.println("Angekommene Nachricht : " + responseMessage.getMessage());
                    if(responseMessage.getMessage().equals("\"true\"")){
                        gameHasAlreadyStartedOnce = true;
                        System.out.println("Buttons wurde reseted!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                    if(responseMessage.getMessage().equals("Game has started")){
                        return true;
                    }
                } else {
                    System.err.println("Fehler: " + statusCode);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return false;
    }
}
