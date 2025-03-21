package com.example;

import com.example.tcm2209.StepperController;
import com.example.tcm2209.TMCDeviceIsBusyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.NotFoundException;
import com.pi4j.Pi4J;
import javafx.util.Pair;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.pwm.Pwm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

@SpringBootApplication
public class Main {
    //Todo: Alles in Packages unterteilen
    //      Api dokumentieren (Welche Endpunkte gibt es? Warum? Nach welchem System?)

    public static Context pi4j =   Pi4J.newAutoContext();;
    static GameService gameService;
    static Camera camera = new Camera();
    static ObjectMapper mapper = new ObjectMapper();
    static Gamemode gamemode;
    static boolean turnHasEnded = false;
    static GameGraphics gameGraphics = new GameGraphics(false);
    static GameFX gameFx = new GameFX();
    static boolean gameRestarted = false;
    static boolean gameChoiceReseted = false;
    static ArrayList<Player> listOfAllPlayerAtTheBeginningOfTheGame;
    static StepperController stepperController;
    static boolean someOneStartedWithBlackJack = false;
    static boolean stepperIsHome = true;
    static ArrayList<Player> blackJackList = new ArrayList<>();
    static ArrayList<Player> listOfAllPlayersWhoPlayTheGame = new ArrayList<>();
    static IKartenMotor cardMotor;
    static Karte oldCard = new Karte();

    //--add-opens=Der.Geraet.Maven/com.example=ALL-UNNAMED --add-reads Der.Geraet.Maven=ALL-UNNAMED
    public static void main(String[] args) throws InterruptedException, TMCDeviceIsBusyException, CloneNotSupportedException {

        ApplicationContext context = SpringApplication.run(Main.class, args);
        gameService = context.getBean(GameService.class);

        stepperController = new StepperController(pi4j);
        stepperController.orientieren();

        cardMotor = new KartenMotor(pi4j);

        Raspberry_Controller raspberryController = new Raspberry_Controller(pi4j);
        gameService.setConnected(true);
        if(gameService.isConnected()) {
            startController(raspberryController);
            startGamePanel();
        }
        while (!gameService.isGameHasEnded() || gameChoiceReseted) {
            resetGameChoice();
            gameService.setConnected(true);
            gameService.setGameHasEnded(false);
            gameChoiceReseted = false;
            registerPlayer();
            listOfAllPlayersWhoPlayTheGame = (ArrayList<Player>) listOfAllPlayerAtTheBeginningOfTheGame.clone();
            while (!gameService.isGameHasEnded() || gameRestarted) {
                cardMotor.readData();
                gameGraphics.setMenuClicked(false);
                gameGraphics.setRestartClicked(false);
                restartTheCurrentgame();
                gameService.setGameStarted(true);
                gameService.setGameHasEnded(false);
                gameRestarted = false;
                initializeGame();
                gameLogic();
            }
        }
    }

    public static void registerPlayer() throws InterruptedException, TMCDeviceIsBusyException, CloneNotSupportedException {
        System.out.println("Spieler werden registriert");
        while (!gameService.isGameStarted()) {
            if(gameService.getAdminPanelRotateStepper().getKey()){
                System.out.println("Sollte drehen");
                if(gameService.getAdminPanelRotateStepper().getValue() == 0)
                    stepperController.orientieren();
//                    System.out.println("Es fähjrt zum Ursprung");
                else
                    rotateStepperMotor(gameService.getAdminPanelRotateStepper().getValue());
                gameService.setAdminPanelRotateStepper(new Pair<>(false, 0));
            }
            if(gameService.isAdminPanelCardThrowActivated()){
                System.out.println("Sollte rauswerfen");
                executeCardThrow(); //Normale Karte
//                cardMotor.werfeKarteAusDealer(); //Dealer karte
                gameService.setAdminPanelCardThrowActivated(false);
            }
            if(gameService.isConnected()) {
                List<String> playerIds = gameService.getListOfAllPlayers().stream()
                        .map(Player::getId)
                        .toList();
                gameFx.updateSeatColor(playerIds);
                if (gameFx.isGameStarted()) {
                    gameService.setCurrentPlayer(new Player("0"));
                    listOfAllPlayerAtTheBeginningOfTheGame = new ArrayList<>();
                    for (Player player : (ArrayList<Player>) gameService.getListOfAllPlayers().clone()) {
                        listOfAllPlayerAtTheBeginningOfTheGame.add((Player) player.clone());
                    }
                    gameService.setGamemode(gameFx.getCurrentGame());
                    gamemode = gameService.getGamemode();
                    gameService.setGameStarted(true);
                    gameFx.setGameStarted(false);

                }
            }
            Thread.sleep(100);
        }
            if(!gameService.isConnected()){
                gameService.setCurrentPlayer(new Player("0"));
                listOfAllPlayerAtTheBeginningOfTheGame = new ArrayList<>();
                for (Player player : (ArrayList<Player>) gameService.getListOfAllPlayers().clone()) {
                    listOfAllPlayerAtTheBeginningOfTheGame.add((Player) player.clone());
                }
                gamemode = gameService.getGamemode();
            }
    }

    private static void gameLogic() throws InterruptedException {
        ArrayList<Player> listOfToRemovingPlayer = new ArrayList<>();
        for(Player player : gameService.getListOfAllPlayers()){
            player.countHand();
            if(player.getKartenhandWert() == 21){
                listOfToRemovingPlayer.add(player);
                System.out.println("Spieler wurde hinzugefügt");
            }
        }
        if(!listOfToRemovingPlayer.isEmpty()) {
            for(Player player : listOfToRemovingPlayer){
                gameService.getListOfAllPlayers().remove(player);
                blackJackList.add(player);
                System.out.println("Spieler wurde entfernt");
                someOneStartedWithBlackJack = true;
            }
        }
        if(gameService.getListOfAllPlayers() != null && !gameService.getListOfAllPlayers().isEmpty()) {
            if (gameService.getListOfAllPlayers().size() >= 2) {
                Collections.sort(gameService.getListOfAllPlayers(), new Comparator<Player>() {
                    @Override
                    public int compare(Player p1, Player p2) {
                        return Integer.compare(Integer.parseInt(p1.getId()), Integer.parseInt(p2.getId()));
                    }
                });
            }
            gameService.setCurrentPlayer(gameService.getListOfAllPlayers().get(0));
        } else{
            executeComputerTurn();
        }
        while (!gameService.isGameHasEnded()) {
            if (gameService.isButtonClickedOnce()) {
                hitEvent();
                gameService.setButtonClickedOnce(false);
                gameService.setButtonClickedTwice(false);
            }
            if (gameService.isButtonClickedTwice()) {
                stayEvent();
                gameService.setButtonClickedOnce(false);
                gameService.setButtonClickedTwice(false);
            }
            if (turnHasEnded) {
                executeNextTurn();
                turnHasEnded = false;
            }
            Thread.sleep(100);
        }
        if (gameService.isConnected()) {
            System.out.println("Spiel Connected");
            while (!gameGraphics.isRestartClicked() && !gameGraphics.isMenuClicked()) {
                System.out.println("Gebe eine Button Anweisung an");
            }

            gameRestarted = gameGraphics.isRestartClicked();
            gameChoiceReseted = gameGraphics.isMenuClicked();
            gameGraphics.setMenuClicked(false);
            gameGraphics.setRestartClicked(false);
        } else {
            System.out.println("Spiel nicht Connected");
            while(!gameService.isGameRestarted() && !gameService.isGameChoiceReseted()){
                System.out.println("Mach mal was");
            }
            gameRestarted = gameService.isGameRestarted();
            gameChoiceReseted = gameService.isGameChoiceReseted();
            gameService.setGameChoiceReseted(false);
            gameService.setGameRestarted(false);
        }
        gameService.setGameHasEnded(true);
        System.out.println("Restart: "+ gameRestarted);
        System.out.println("Reset: "+ gameChoiceReseted);
    }

    public static void checkForOldCard() throws InterruptedException {
        Karte karte = new Karte();

        for (int i = 0; i < 10; i++) {


            BufferedImage bufferedImage = camera.captureImage();

            if (bufferedImage != null) {
                try {
                    // Dekodiere den QR-Code
                    String decodedText = camera.decodeQRCode(bufferedImage);
                    if (decodedText != null) {
                        System.out.println("Decoded text: " + decodedText);
                        karte = mapper.readValue(decodedText, Karte.class);
                        break;
                    } else {
                        System.out.println("QR-Code nicht gefunden");
                    }
                } catch (Exception e) {
                    System.out.println("Fehler");
                }
            } else {
                System.out.println("Fehler: Kein Bild von der Webcam erhalten.");
            }
        }
        while(oldCard.equals(karte)) {
            executeCardThrow();
            BufferedImage bufferedImage = camera.captureImage();

            if (bufferedImage != null) {
                try {
                    // Dekodiere den QR-Code
                    String decodedText = camera.decodeQRCode(bufferedImage);
                    if (decodedText != null) {
                        System.out.println("Decoded text: " + decodedText);
                        karte = mapper.readValue(decodedText, Karte.class);
                    } else {
                        System.out.println("QR-Code nicht gefunden");
                    }
                } catch (Exception e) {
                    System.out.println("Fehler");
                }
            } else {
                System.out.println("Fehler: Kein Bild von der Webcam erhalten.");
            }
        }

    }


    public static void executeNextTurn() throws InterruptedException {
        Player deleteThisPlayer = null;
        for(Player player : listOfAllPlayerAtTheBeginningOfTheGame){
            System.out.println("Alt: " +player.getId());
        }

        for(Player player : listOfAllPlayersWhoPlayTheGame){
            System.out.println("Test Alt: " +player.getId());
        }
        if (gameService.getCurrentPlayer().getKartenhandWert() > 21) {
            deleteThisPlayer = gameService.getCurrentPlayer();
            gameService.setCurrenPlayerIndex(gameService.getCurrenPlayerIndex() - 1);
            gameService.getListOfAllPlayers().remove(deleteThisPlayer);
//            listOfAllPlayerAtTheBeginningOfTheGame.add(deleteThisPlayer);
            for(Player player : listOfAllPlayerAtTheBeginningOfTheGame){
                System.out.println("Neu: " +player.getId());
            }
            for(Player player : listOfAllPlayersWhoPlayTheGame){
                System.out.println("Test Neu: " +player.getId());
            }
        }
        if (gameService.getCurrenPlayerIndex() + 1 >= gameService.getListOfAllPlayers().size()) {
            if (deleteThisPlayer != null) {
                gameService.getListOfAllPlayers().remove(deleteThisPlayer);
            }
            executeComputerTurn();
        } else {
            gameService.setCurrenPlayerIndex(gameService.getCurrenPlayerIndex() + 1);
            gameService.setCurrentPlayer(gameService.getListOfAllPlayers().get(gameService.getCurrenPlayerIndex()));
        }
    }

    public static void executeComputerTurn() throws InterruptedException {
        gameService.setCurrentPlayer(new Player("0"));
        switch (gamemode) {
            case BLACKJACK -> {
                rotateStepperMotor(13);
                gameService.setNumberOfCardFaceup(gameService.getNumberOfCardFaceup() +1);
                System.out.println("Computer Turn ist dran");
                if(gameService.isConnected()) {
                    gameGraphics.removeFaceDownCard();
                    gameGraphics.addCardToTable(gameService.getDealer().getDealerHand().get(1));
                }
                System.out.println("Karte wurde hinzugefügt");
                gameService.getDealer().countHand();
                Thread.sleep(5000);
                if (gameService.getListOfAllPlayers().isEmpty() && !someOneStartedWithBlackJack) {

                } else {
                    while (gameService.getDealer().getDealerHandWert() < 17) {
                        gameService.setNumberOfCardFaceup(gameService.getNumberOfCardFaceup() +1);
                        executeCameraScan();
                        executeCardThrow();
                        giveDealerNextCard();
                        checkForOldCard();
                        if(gameService.isConnected()) {
                            gameGraphics.addCardToTable(gameService.getDealer().getDealerHand().get(gameService.getDealer().getDealerHand().size() - 1));
                        }
                        gameService.getDealer().countHand();
                        Thread.sleep(5000);
                    }
                }
                int dealerHandWert = gameService.getDealer().getDealerHandWert();
                if(someOneStartedWithBlackJack){
                    for(Player player : blackJackList){
                        gameService.getListOfAllPlayers().add(player);
                        System.out.println("Ein Spieler wurde wieder hinzugefügt");
                    }
                }
                for (Player player : gameService.getListOfAllPlayers()) {
                    player.countHand();
                    if (dealerHandWert > 21) {
                        //Alle gewinnen
                        gameService.getMapOfAllWinners().put("Spieler " + player.getId(), "hat gewonnen");
                    } else if (dealerHandWert == 21) {
                        //schauen ob jemand mitziehen kann
                        if (player.getKartenhandWert() == 21) {
                            gameService.getMapOfAllWinners().put("Spieler " + player.getId(), "hat unentschieden gespielt");
                        }
                    } else {
                        //ganz normal vergleichen
                        if (player.getKartenhandWert() > dealerHandWert) {
                            gameService.getMapOfAllWinners().put("Spieler " + player.getId(), "hat gewonnen");
                        } else if (player.getKartenhandWert() == dealerHandWert) {
                            gameService.getMapOfAllWinners().put("Spieler " + player.getId(), "hat unentschieden gespielt");
                        }
                    }
                }
                for (String winner : gameService.getMapOfAllWinners().keySet()) {
                    System.out.println(winner + " " + gameService.getMapOfAllWinners().get(winner));
                }
                if (gameService.getMapOfAllWinners() == null || gameService.getMapOfAllWinners().isEmpty()) {
                    System.out.println("Alle haben verloren");
                    gameService.getMapOfAllWinners().put("Alle", "haben verloren");
                }
                if (gameService.isConnected())
                    gameGraphics.showGameResults(gameService.getMapOfAllWinners());
                gameService.setGameHasEnded(true);
            }
            case POKER -> {
                if(gameService.getDealer().getDealerHand() == null || gameService.getDealer().getDealerHand().isEmpty()){
                    for (int i = 0; i < 3; i++) {
                        rotateStepperMotor(13);
                        gameService.setNumberOfCardFaceup(5);
                        executeCameraScan();
                        Thread.sleep(100);
                        executeCardThrow();
                        giveDealerNextCard();
                        checkForOldCard();
                        if (gameService.isConnected()) {
                            gameGraphics.addCardToTable(gameService.getDealer().getDealerHand().get(i));
                        }
                        System.out.println("Jetzt Karte entnehmen");
                        Thread.sleep(3000);
                    }
                } else {
                    gameService.setCurrenPlayerIndex(0);
                    rotateStepperMotor(13);
                    executeCameraScan();
                    executeCardThrow();
                    giveDealerNextCard();
                    checkForOldCard();
                    if (gameService.isConnected()) {
                        gameGraphics.addCardToTable(gameService.getDealer().getDealerHand().get(gameService.getDealer().getDealerHand().size() - 1));
                    }
                    if (gameService.getDealer().getDealerHand().size() >= 5) {
                        Thread.sleep(10000);
                        if (gameService.isConnected())
                            gameGraphics.showGameResults(gameService.getMapOfAllWinners());
                        gameService.setGameHasEnded(true);
                    }
                }
                //Taster auf High setzen
            }
        }
    }

    public static void hitEvent() throws InterruptedException {
        switch (gamemode) {
            case BLACKJACK -> {
                rotateStepperMotor(Integer.parseInt(gameService.getCurrentPlayer().getId()));
                executeCameraScan();
                executeCardThrow();
                giveCurrentPlayerNextCard();
                checkForOldCard();
                gameService.getCurrentPlayer().countHand();
                System.out.println(gameService.getCurrentPlayer().getKartenhandWert());
                if (gameService.getCurrentPlayer().getKartenhandWert() >= 21) {
                    stayEvent();
                }
            }
            case POKER -> {
                executeComputerTurn();
            }
        }
    }

    private static void giveDealerNextCard() {

        if (gameService.getDealer().getDealerHand() == null) {
            gameService.getDealer().setDealerHand(new ArrayList<Karte>());
            gameService.getDealer().getDealerHand().add(gameService.getNextCardInDeck());
        } else {
            gameService.getDealer().getDealerHand().add(gameService.getNextCardInDeck());
        }
    }

    private static void executeCameraScan() {
//        Karte karte = new Karte("10", "Herz", "Herz 10");
//        gameService.setNextCardInDeck(karte);

        for (int i = 0; i < 10; i++) {
            BufferedImage bufferedImage = camera.captureImage();

            if (bufferedImage != null) {
                try {
                    // Dekodiere den QR-Code
                    String decodedText = camera.decodeQRCode(bufferedImage);
                    if (decodedText != null) {
                        System.out.println("Decoded text: " + decodedText);
                        Karte karte = mapper.readValue(decodedText, Karte.class);
                        oldCard = karte;
                        gameService.setNextCardInDeck(karte);
                            break;
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
    }

    private static void executeCardThrow() throws InterruptedException {
        cardMotor.werfeKarteAus();
        System.out.println("Karte wurde gewurfen");
    }

    private static void giveCurrentPlayerNextCard() {
        if (gameService.getCurrentPlayer().getKartenhand() == null) {
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
        switch (gamemode) {
            case BLACKJACK -> {

            }
            case POKER -> {
                executeComputerTurn();
            }
        }
    }

    public static void rotateStepperMotor(int playerID) {
        double angle = (-25 * (playerID-1)) -12.5;
        if(playerID == 13){
            angle = -90;
        }
        if(playerID>=4 && playerID <=6){
            angle = (-25 * playerID)-17.5;
        }

        try{
            stepperController.turn((int) angle);
            System.out.println("Es fährt zum Winkel: " + angle);
        } catch(Exception e){
            e.printStackTrace();
        }
        //stepperMotor.high();
        //Thread.sleep(angle/10);
        //stepperMotor.low();
    }

//    public static void controllerConfig(){
//        var stepperMotorConfig = DigitalOutput.newConfigBuilder(pi4j)
//                .name("Stepper Motor")
//                .id("Stepper Motor ID")
//                .address(24) //passende adresse einfügen
//                .initial(DigitalState.LOW)
//                .onState(DigitalState.HIGH);
//        stepperMotor = pi4j.create(stepperMotorConfig);
//
//
//
//        discardMotorIn1 = pi4j.dout().create(DigitalOutput.newConfigBuilder(pi4j)
//                .id("IN1")
//                .name("Motor IN1")
//                .address(MappingForAdress.getMotorAdress("1"))
//                .shutdown(DigitalState.LOW)
//                .initial(DigitalState.LOW)
//                .provider("pigpio-digital-output"));
//
//        discardMotorIn2 = pi4j.dout().create(DigitalOutput.newConfigBuilder(pi4j)
//                .id("IN2")
//                .name("Motor IN2")
//                .address(MappingForAdress.getMotorAdress("2"))
//                .shutdown(DigitalState.LOW)
//                .initial(DigitalState.LOW)
//                .provider("pigpio-digital-output"));
//
//        connectionInput = pi4j.create(DigitalInput.newConfigBuilder(pi4j)
//                .name("Connection Input")
//                .id("Connection Input ID")
//                .address(MappingForAdress.getConnectionAdress())
//                .pull(PullResistance.PULL_DOWN)
//                .debounce(150L));
//
//    }

//das ist ein test2

    private static void initializeGame() throws InterruptedException {
        switch (gamemode) {
            case BLACKJACK -> {
                System.out.println("Karten werden für den Anfang ausgeteilt");
                for (int i = 0; i < 2; i++) {
                    rotateStepperMotor(13);
                    executeCameraScan();
                    Thread.sleep(100);
                    executeCardThrow();
                    giveDealerNextCard();
                    checkForOldCard();
                    if (gameService.isConnected()) {
                        if (i == 0)
                            gameGraphics.addCardToTable(gameService.getDealer().getDealerHand().get(0));
                        else
                            gameGraphics.addBeginningCards();
                    }
                    System.out.println("Jetzt Karte entnehmen");
                    Thread.sleep(3000);
                }

                for (Player player : gameService.getListOfAllPlayers()) {
                    gameService.setCurrentPlayer(player);
                    for (int i = 0; i < 2; i++) {
                        rotateStepperMotor(Integer.parseInt(gameService.getCurrentPlayer().getId()));
                        executeCameraScan();
                        Thread.sleep(100);
                        executeCardThrow();
                        giveCurrentPlayerNextCard();
                        checkForOldCard();
                        System.out.println("Jetzt Karte entnehmen");
                        Thread.sleep(3000);
                    }
                }
                gameService.setButtonClickedOnce(false);
                gameService.setButtonClickedTwice(false);
            }
            case POKER -> {
                Raspberry_Controller.isGameModePoker = true;
                for (Player player : gameService.getListOfAllPlayers()) {
                    gameService.setCurrentPlayer(player);
                    for (int i = 0; i < 2; i++) {
                        rotateStepperMotor(Integer.parseInt(gameService.getCurrentPlayer().getId()));
                        executeCameraScan();
                        Thread.sleep(100);
                        executeCardThrow();
                        giveCurrentPlayerNextCard();
                        checkForOldCard();
                        System.out.println("Jetzt Karte entnehmen");
                        Thread.sleep(3000);
                    }
                }
                gameService.setCurrentPlayer(new Player("0"));
                gameService.setButtonClickedOnce(false);
                gameService.setButtonClickedTwice(false);
            }
        }
            System.out.println("Karten wurden ausgeteilt");

    }

    private static void startGamePanel() {
        new Thread(() -> {
            gameFx.launchApp();
        }).start();
    }

    private static void startController(Raspberry_Controller controller) {
        new Thread(() -> {
            try {
                controller.execute();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static void restartTheCurrentgame() {
        gameRestarted = false;
        oldCard = new Karte();
        someOneStartedWithBlackJack = false;
        gameChoiceReseted = false;
        blackJackList = new ArrayList<>();
        gameService.setListOfAllPlayers((ArrayList<Player>) listOfAllPlayersWhoPlayTheGame.clone());
        for(Player player : listOfAllPlayerAtTheBeginningOfTheGame){
            System.out.println(player.getId());
        }
        gameService.getDealer().resetDealer();
        for (Player player : gameService.getListOfAllPlayers()) {
            player.resetPlayer();
        }

        turnHasEnded = false;
        gameService.restartTheCurrentGame();
    }

    private static void resetGameChoice() {
        gameRestarted = false;
        gameChoiceReseted = false;
        blackJackList = new ArrayList<>();
        someOneStartedWithBlackJack = false;
        oldCard = new Karte();

        turnHasEnded = false;
        gameService.resetGameChoice();
    }
}
