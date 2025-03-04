package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

@SpringBootApplication
public class Main {
    static Context pi4j =  Pi4J.newAutoContext();
    static DigitalOutput stepperMotor;
    static DigitalOutput discardMotorIn1;
    static DigitalOutput discardMotorIn2;
    static DigitalInput connectionInput;
    static GameService gameService;
    static Camera camera = new Camera();
    static ObjectMapper mapper = new ObjectMapper();
    static Gamemode gamemode;
    static boolean turnHasEnded = false;
    static BlackJack blackJackOberflaeche = new BlackJack();



    //--add-opens=Der.Geraet.Maven/com.example=ALL-UNNAMED
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
        System.out.println("Spieler werden registriert");
        while(true){
            Thread.sleep(30000);
            gameService.setGameStarted(true);
            if(gameService.isGameStarted()){
                gameService.setCurrentPlayer(new Player("0"));
                gamemode = gameService.getGamemode();
                //alle Taster low
                break;
            }
        }
    }

    private static void gameLogic() throws InterruptedException {
        if(gameService.getListOfAllPlayers().size()>=2) {
            Collections.sort(gameService.getListOfAllPlayers(), new Comparator<Player>() {
                @Override
                public int compare(Player p1, Player p2) {
                    return Integer.compare(Integer.parseInt(p1.getId()), Integer.parseInt(p2.getId()));
                }
            });
        }
        gameService.setCurrentPlayer(gameService.getListOfAllPlayers().get(0));
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
            if(deleteThisPlayer != null){
                gameService.getListOfAllPlayers().remove(deleteThisPlayer);
            }
        }
    }

    public static void executeComputerTurn() throws InterruptedException {
        gameService.setCurrentPlayer(new Player("0"));
        switch (gamemode){
            case BLACKJACK -> {
                System.out.println("Computer Turn ist dran");
                blackJackOberflaeche.removeFaceDownCard();
                blackJackOberflaeche.addCardToTable(gameService.getDealer().getDealerHand().get(1));
                System.out.println("Karte wurde hinzugefügt");
                gameService.getDealer().countHand();
                Thread.sleep(2000);
                if(gameService.getListOfAllPlayers().isEmpty()){

                } else {
                    while (gameService.getDealer().getDealerHandWert() < 17) {
                        rotateStepperMotor(1000);
                        System.out.println("Karte bekommen");
                        giveDealerNextCard();
                        blackJackOberflaeche.addCardToTable(gameService.getDealer().getDealerHand().get(gameService.getDealer().getDealerHand().size()-1));
                        gameService.getDealer().countHand();
                        executeCameraScan();
                        Thread.sleep(1000);
                    }
                }
                int dealerHandWert = gameService.getDealer().getDealerHandWert();
                for (Player player : gameService.getListOfAllPlayers()){
                    if(dealerHandWert > 21){
                        //Alle gewinnen
                        gameService.getMapOfAllWinners().put(player.getId(), "hat gewonnen");
                    } else if (dealerHandWert == 21) {
                        //schauen ob jemand mitziehen kann
                        if(player.getKartenhandWert() == 21){
                            gameService.getMapOfAllWinners().put(player.getId(), "hat unentschieden gespielt");
                        }
                    } else{
                        //ganz normal vergleichen
                        if (player.getKartenhandWert() > dealerHandWert){
                            gameService.getMapOfAllWinners().put(player.getId(), "hat gewonnen");
                        } else if (player.getKartenhandWert() == dealerHandWert) {
                            gameService.getMapOfAllWinners().put(player.getId(), "hat unentschieden gespielt");
                        }
                    }
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
        } else {
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
                .address(24) //passende adresse einfügen
                .initial(DigitalState.LOW)
                .onState(DigitalState.HIGH);
        stepperMotor = pi4j.create(stepperMotorConfig);



        discardMotorIn1 = pi4j.dout().create(DigitalOutput.newConfigBuilder(pi4j)
                .id("IN1")
                .name("Motor IN1")
                .address(MappingForAdress.getMotorAdress("1"))
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output"));

        discardMotorIn2 = pi4j.dout().create(DigitalOutput.newConfigBuilder(pi4j)
                .id("IN2")
                .name("Motor IN2")
                .address(MappingForAdress.getMotorAdress("2"))
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output"));

        connectionInput = pi4j.create(DigitalInput.newConfigBuilder(pi4j)
                .name("Connection Input")
                .id("Connection Input ID")
                .address(MappingForAdress.getConnectionAdress())
                .pull(PullResistance.PULL_DOWN)
                .debounce(150L));

    }

//das ist ein test2

    private static void initializeGame() throws InterruptedException {

        executeCameraScan();
        switch (gamemode){
            case BLACKJACK -> {
                startGamePanel();
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

    private static void startGamePanel() {
        new Thread (() -> {
            blackJackOberflaeche.launchApp();
        }).start();
    }
}
