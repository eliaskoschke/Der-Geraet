package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

@SpringBootApplication
public class Main {
    static Context pi4j =  Pi4J.newAutoContext();
    static DigitalOutput stepperMotor;
    static DigitalOutput discardMotorIn1;
    static DigitalOutput discardMotorIn2;
    static DigitalOutput cameraOutput;
    static DigitalInput connectionInput;
    static GameService gameService;
    static Camera camera = new Camera();
    static ObjectMapper mapper = new ObjectMapper();
    static Gamemode gamemode;
    static boolean turnHasEnded = false;
    static HashMap<String, DigitalOutput> playerButtonMap = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        controllerConfig();
        ApplicationContext context = SpringApplication.run(Main.class, args);
        gameService = context.getBean(GameService.class);
        //Initialize Methode für ersten kamera Scan etc.
//        if(connectionInput.isHigh()){
//            gameService.setConnected(true);
//        }
        registerPlayer();
        initializeGame();
        gameLogic();
    }

    public static void registerPlayer() throws InterruptedException {
        gameService.setWaiting(true);
        System.out.println("Spieler werden registriert");
        for(DigitalOutput output : playerButtonMap.values()){
            output.high();
        }
        Thread.sleep(2000);
        gameService.setWaiting(false);
        while(true){
            Thread.sleep(15000);
            for (Player player : gameService.getListOfAllPlayers()){
                playerButtonMap.get(player.getId()).low();
            }
            gameService.setGameStarted(true);
            if(gameService.isGameStarted()){
                gamemode = gameService.getGamemode();
                for(DigitalOutput output : playerButtonMap.values()){
                    output.low();
                }
                //alle Taster low
                break;
            }
        }
    }

    private static void gameLogic() throws InterruptedException {
        Collections.sort(gameService.getListOfAllPlayers(), new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return Integer.compare(Integer.parseInt(p1.getId()),Integer.parseInt(p2.getId()));
            }
        });
        gameService.setCurrentPlayer(gameService.getListOfAllPlayers().get(0));
        activateCurrentPlayerButton();
        while(true){
            if(gameService.isButtonClickedOnce()){
                hitEvent();
                gameService.setButtonClickedOnce(false);
            }
            if(gameService.isButtonClickedTwice()){
                //Taster des aktuellen Spielers Low
                stayEvent();
                gameService.setButtonClickedTwice(false);
            }
            if (turnHasEnded) {
                executeNextTurn();
                turnHasEnded = false;
            }
            Thread.sleep(100);
        }
    }

    private static void activateCurrentPlayerButton() {
        gameService.setWaiting(true);
        playerButtonMap.get(gameService.getCurrentPlayer().getId()).high();
        gameService.setWaiting(false);
    }

    public static void executeNextTurn() throws InterruptedException {
        if(gameService.getCurrenPlayerIndex()+1 >= gameService.getListOfAllPlayers().size()){
            executeComputerTurn();
        } else{
            Player deleteThisPlayer = null;
            if(gameService.getCurrentPlayer().getKartenhandWert() > 21){
                deleteThisPlayer = gameService.getCurrentPlayer();
            }
            gameService.setCurrenPlayerIndex(gameService.getCurrenPlayerIndex()+1);
            gameService.setCurrentPlayer(gameService.getListOfAllPlayers().get(gameService.getCurrenPlayerIndex()));
            activateCurrentPlayerButton();
            if(deleteThisPlayer != null){
                gameService.getListOfAllPlayers().remove(deleteThisPlayer);
            }
        }
    }

    public static void executeComputerTurn() throws InterruptedException {
        switch (gamemode){
            case BLACKJACK -> {
                while(gameService.getDealer().getDealerHandWert() < 17){
                    rotateStepperMotor(1000);
                    System.out.println("Karte bekommen");
                    giveDealerNextCard();
                    gameService.getDealer().countHand();
                    executeCameraScan();
                    Thread.sleep(1000);
                }
            }
            case POKER -> {

                gameService.setCurrenPlayerIndex(0);
                //Taster auf High setzen
            }
        }
    }

    public static void hitEvent() throws InterruptedException {
        switch (gamemode){
            case BLACKJACK -> {
                rotateStepperMotor(1000);
                executeCardThrow();
                giveCurrentPlayerNextCard();
                executeCameraScan();
                gameService.getCurrentPlayer().countHand();
                System.out.println(gameService.getCurrentPlayer().getKartenhandWert());
                if(gameService.getCurrentPlayer().getKartenhandWert() >= 21){
                    stayEvent();
                }
            }
            case POKER -> {
                turnHasEnded = true;
            }
        }
    }

    private static void giveDealerNextCard(){
        if(gameService.getDealer().getDealerHand() == null){
            gameService.getDealer().setDealerHand(new ArrayList<Karte>());
            gameService.getDealer().getDealerHand().add(gameService.getNextCardInDeck());
        } else{
            gameService.getDealer().getDealerHand().add(gameService.getNextCardInDeck());
        }
    }

    private static void executeCameraScan(){
        BufferedImage bufferedImage=camera.captureImage();

        if (bufferedImage != null) {
            try {
                // Dekodiere den QR-Code
                String decodedText = camera.decodeQRCode(bufferedImage);
                if (decodedText != null) {
                    System.out.println("Decoded text: " + decodedText);
                    Karte karte = mapper.readValue(decodedText, Karte.class);
                    gameService.setNextCardInDeck(karte);
                } else {
                    System.out.println("QR-Code nicht gefunden");
                }
            } catch (Exception e) {
                System.out.println("Fehler beim Dekodieren des QR-Codes: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Fehler: Kein Bild von der Webcam erhalten.");
        }
    }



    private static void executeCardThrow() throws InterruptedException {
        discardMotorIn1.low();
        discardMotorIn2.high();
        System.out.println("Motor wurde angesteuert");
        Thread.sleep(1000);
        discardMotorIn1.low();
        discardMotorIn2.low();
    }

    private static void giveCurrentPlayerNextCard() {
        if(gameService.getCurrentPlayer().getKartenhand() == null){
//                        gameService.getDealer().setDealerHand(new ArrayList<Karte>());
//                        gameService.getDealer().getDealerHand().add(karte);
            gameService.getCurrentPlayer().setKartenhand(new ArrayList<Karte>());
            gameService.getCurrentPlayer().getKartenhand().add(gameService.getNextCardInDeck());
        } else {
//                        gameService.getDealer().getDealerHand().add(karte);
            gameService.getCurrentPlayer().getKartenhand().add(gameService.getNextCardInDeck());
        }
    }

    public static void stayEvent() throws InterruptedException {
        turnHasEnded = true;
        playerButtonMap.get(gameService.getCurrentPlayer().getId()).low();
        switch(gamemode){
            case BLACKJACK -> {

            }
            case POKER -> {
                gameService.getCurrentPlayer().setKartenhandWert(33);
            }
        }
    }
    public static void rotateStepperMotor(int angle) throws InterruptedException {
        stepperMotor.high();
        Thread.sleep(angle/10);
        stepperMotor.low();
    }

    public static void controllerConfig(){
        var stepperMotorConfig = DigitalOutput.newConfigBuilder(pi4j)
                .name("Stepper Motor")
                .id("Stepper Motor ID")
                .address(21) //passende adresse einfügen
                .initial(DigitalState.LOW)
                .onState(DigitalState.HIGH);
        stepperMotor = pi4j.create(stepperMotorConfig);

        int in1PinNumber = 18; // Beispiel-Pin-Nummer für IN1
        int in2PinNumber = 23; // Beispiel-Pin-Nummer für IN2

        discardMotorIn1 = pi4j.dout().create(DigitalOutput.newConfigBuilder(pi4j)
                .id("IN1")
                .name("Motor IN1")
                .address(in1PinNumber)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output"));

        discardMotorIn2 = pi4j.dout().create(DigitalOutput.newConfigBuilder(pi4j)
                .id("IN2")
                .name("Motor IN2")
                .address(in2PinNumber)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output"));
//        connectionInput = pi4j.create(DigitalInput.newConfigBuilder(pi4j)
//                .name("Connection Input")
//                .id("Connection Input ID")
//                .address(22)
//                .pull(PullResistance.PULL_DOWN)
//                .debounce(150L));

        addButtonOutputs("1", 20);
        addButtonOutputs("2", 16);
        addButtonOutputs("3", 12);
        addButtonOutputs("4", 7);
        addButtonOutputs("5", 8);
        addButtonOutputs("6", 25);

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

    private static void initializeGame() throws InterruptedException {

        executeCameraScan();
        switch (gamemode){
            case BLACKJACK -> {
                System.out.println("Karten werden für den Anfang ausgeteilt");
                for (int i = 0; i < 2; i++) {
                    rotateStepperMotor(1000);
                    executeCardThrow();
                    giveDealerNextCard();
                    executeCameraScan();
                    Thread.sleep(500);
                }
                for (Player player : gameService.getListOfAllPlayers()){
                    gameService.setCurrentPlayer(player);
                    for (int i = 0; i < 2; i++) {
                        rotateStepperMotor(1000);
                        executeCardThrow();
                        giveCurrentPlayerNextCard();
                        executeCameraScan();
                        Thread.sleep(500);
                    }
                }

            }
            case POKER -> {

            }
        }

        System.out.println("Karten wurden ausgeteilt");
    }
}