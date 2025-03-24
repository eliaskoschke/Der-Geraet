package com.example;

import com.example.tcm2209.StepperController;
import com.example.tcm2209.TMCDeviceIsBusyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import javafx.util.Pair;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.*;

@Service
public class GameService {
    //Todo: static weg. WARUM?????

    public static Context pi4j = Pi4J.newAutoContext();
    static Camera camera = new Camera();
    static ObjectMapper mapper = new ObjectMapper();
    static Gamemode gamemode = Gamemode.BLACKJACK;
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

    private static Pair<Boolean, Integer> adminPanelRotateStepper = new Pair<>(false, 0);

    private static boolean adminPanelCardThrowActivated = false;

    private static HashMap<String, String> mapOfAllWinners = new HashMap<>();

    private static boolean connected = false;

    private static int numberOfCardFaceup = 1;

    private static boolean buttonClickedOnce = false;

    private static boolean buttonClickedTwice = false;

    private static String getMessage = "";

    private static boolean gameStarted = false;

    static boolean gameReset = false;

    private static boolean gameHasEnded = false;

    static Karte nextCardInDeck;

    static int playerAtReset = 0;

    static int playerGotReseted = 0;

    private static Player currentPlayer = new Player();

    private static ArrayList<Player> listOfAllPlayers = new ArrayList<>();

    private static int currenPlayerIndex = 0;

    static Computer dealer = new Computer();

    public GameService() throws TMCDeviceIsBusyException {
        stepperController = new StepperController(pi4j);
        stepperController.orientieren();

        cardMotor = new KartenMotor(pi4j);

        Raspberry_Controller raspberryController = new Raspberry_Controller(pi4j);
        connected = (true);
        if(connected) {
            startController(raspberryController);
            startGamePanel();
        }
    }

    public static void gameEnded() throws InterruptedException, TMCDeviceIsBusyException {
        if (connected) {
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
            while(!gameRestarted && !gameChoiceReseted){
                System.out.println("Mach mal was");
            }
        }
        if(gameChoiceReseted){
            resetGameChoice();
            connected = (true);
            gameHasEnded = (false);
            gameChoiceReseted = false;
            GameFX.setPlayerList(new ArrayList<String>());
        } else{
            cardMotor.readData();
                gameGraphics.setMenuClicked(false);
                gameGraphics.setRestartClicked(false);
                restartTheCurrentgame();
                gameStarted = (true);
                gameHasEnded = (false);
                gameRestarted = false;
                initializeGame();
        }
        System.out.println("Restart: "+ gameRestarted);
        System.out.println("Reset: "+ gameChoiceReseted);
        gameChoiceReseted = (false);
        gameRestarted = (false);
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

    public static void startGame() throws InterruptedException, CloneNotSupportedException, TMCDeviceIsBusyException {
        for(Player player : listOfAllPlayers){
            System.out.println("Spieler " + player.getId() +" ist im Spiel.");
        }
        if(connected){
            if (gameFx.isGameStarted()) {
                currentPlayer = new Player("0");
                listOfAllPlayerAtTheBeginningOfTheGame = new ArrayList<>();
                for (Player player : (ArrayList<Player>) listOfAllPlayers.clone()) {
                    listOfAllPlayerAtTheBeginningOfTheGame.add((Player) player.clone());
                }
                gamemode = gameFx.getCurrentGame();
                gameStarted = true;
                gameFx.setGameStarted(false);
            }
        } else{
            currentPlayer = new Player("0");
            listOfAllPlayerAtTheBeginningOfTheGame = new ArrayList<>();
            for (Player player : (ArrayList<Player>) listOfAllPlayers.clone()) {
                listOfAllPlayerAtTheBeginningOfTheGame.add((Player) player.clone());
            }
        }
        listOfAllPlayersWhoPlayTheGame = (ArrayList<Player>) listOfAllPlayerAtTheBeginningOfTheGame.clone();
        for(Player player : listOfAllPlayers){
            System.out.println("Spieler " + player.getId() +" ist immernoch im Spiel.");
        }
        cardMotor.readData();
        gameGraphics.setMenuClicked(false);
        gameGraphics.setRestartClicked(false);
        restartTheCurrentgame();
        for(Player player : listOfAllPlayers){
            System.out.println("Spieler " + player.getId() +" ist nach dem reset im Spiel.");
        }
        gameStarted = true;
        gameHasEnded = false;
        gameRestarted = false;
        initializeGame();
    }

    public static void rotateStepperMotor(int playerID) throws TMCDeviceIsBusyException {
        if (playerID == 0) {
            stepperController.orientieren();
        } else {
            int angle = (-25 * (playerID - 1)) - 12;
            if (playerID >= 13) {
                angle = -90;
            } else {
                if (playerID >= 4 && playerID <= 6) {
                    angle = (-25 * playerID) - 17;
                }
            }

            try {
                System.out.println("Es fährt zum Winkel: " + angle);
                stepperController.turn( angle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void hitEvent() throws InterruptedException, TMCDeviceIsBusyException {
        switch (gamemode) {
            case BLACKJACK -> {
                rotateStepperMotor(Integer.parseInt(currentPlayer.getId()));
                executeCameraScan();
                executeCardThrow();
                giveCurrentPlayerNextCard();
                checkForOldCard();
                currentPlayer.countHand();
                System.out.println(currentPlayer.getKartenhandWert());
                if (currentPlayer.getKartenhandWert() >= 21) {
                    stayEvent();
                }
            }
            case POKER -> {
                executeComputerTurn();
            }
        }
    }

    public static void registerPlayer() throws CloneNotSupportedException, InterruptedException {
        if (connected) {
            List<String> playerIds = listOfAllPlayers.stream()
                    .map(Player::getId)
                    .toList();
            gameFx.updateSeatColor(playerIds);
        }
        for(Player player : listOfAllPlayers){
            System.out.println("Spieler " + player.getId() +" wurde registriert");
        }
    }

    public static void stayEvent() throws InterruptedException, TMCDeviceIsBusyException {
        turnHasEnded = true;
        switch (gamemode) {
            case BLACKJACK -> {
                //stepperController.orientieren();
                //Thread.sleep(500);
                executeNextTurn();
            }
            case POKER -> {
                executeComputerTurn();
            }
        }
    }

    public static void updateCardMotorDatabase(){
        cardMotor.readData();
    }

    public static void executeNextTurn() throws InterruptedException, TMCDeviceIsBusyException {
        Player deleteThisPlayer = null;
        for (Player player : listOfAllPlayerAtTheBeginningOfTheGame) {
            System.out.println("Alt: " + player.getId());
        }

        for (Player player : listOfAllPlayersWhoPlayTheGame) {
            System.out.println("Test Alt: " + player.getId());
        }
        if (currentPlayer.getKartenhandWert() > 21) {
            deleteThisPlayer = currentPlayer;
            currenPlayerIndex = currenPlayerIndex - 1;
            listOfAllPlayers.remove(deleteThisPlayer);
            for (Player player : listOfAllPlayerAtTheBeginningOfTheGame) {
                System.out.println("Neu: " + player.getId());
            }
            for (Player player : listOfAllPlayersWhoPlayTheGame) {
                System.out.println("Test Neu: " + player.getId());
            }
        }
        if (currenPlayerIndex + 1 >= listOfAllPlayers.size()) {
            if (deleteThisPlayer != null) {
                listOfAllPlayers.remove(deleteThisPlayer);
            }
            executeComputerTurn();
        } else {
            currenPlayerIndex = currenPlayerIndex + 1;
            currentPlayer = listOfAllPlayers.get(currenPlayerIndex);
        }
    }

    public static void executeComputerTurn() throws InterruptedException, TMCDeviceIsBusyException {
        currentPlayer = new Player("0");
        switch (gamemode) {
            case BLACKJACK -> {
                rotateStepperMotor(13);
                numberOfCardFaceup = numberOfCardFaceup + 1;
                System.out.println("Computer Turn ist dran");
                if (connected) {
                    gameGraphics.removeFaceDownCard();
                    gameGraphics.addCardToTable(dealer.getDealerHand().get(1));
                }
                System.out.println("Karte wurde hinzugefügt");
                dealer.countHand();
                Thread.sleep(5000);
                if (listOfAllPlayers.isEmpty() && !someOneStartedWithBlackJack) {

                } else {
                    while (dealer.getDealerHandWert() < 17) {
                        numberOfCardFaceup = numberOfCardFaceup + 1;
                        executeCameraScan();
                        executeCardThrow();
                        giveDealerNextCard();
                        checkForOldCard();
                        if (connected) {
                            gameGraphics.addCardToTable(dealer.getDealerHand().get(dealer.getDealerHand().size() - 1));
                        }
                        dealer.countHand();
                        Thread.sleep(5000);
                    }
                }
                int dealerHandWert = dealer.getDealerHandWert();
                if (someOneStartedWithBlackJack) {
                    for (Player player : blackJackList) {
                        listOfAllPlayers.add(player);
                        System.out.println("Ein Spieler wurde wieder hinzugefügt");
                    }
                }
                for (Player player : listOfAllPlayers) {
                    player.countHand();
                    if (dealerHandWert > 21) {
                        mapOfAllWinners.put("Spieler " + player.getId(), "hat gewonnen");
                    } else if (dealerHandWert == 21) {
                        if (player.getKartenhandWert() == 21) {
                            mapOfAllWinners.put("Spieler " + player.getId(), "hat unentschieden gespielt");
                        }
                    } else {
                        if (player.getKartenhandWert() > dealerHandWert) {
                            mapOfAllWinners.put("Spieler " + player.getId(), "hat gewonnen");
                        } else if (player.getKartenhandWert() == dealerHandWert) {
                            mapOfAllWinners.put("Spieler " + player.getId(), "hat unentschieden gespielt");
                        }
                    }
                }
                for (String winner : mapOfAllWinners.keySet()) {
                    System.out.println(winner + " " + mapOfAllWinners.get(winner));
                }
                if (mapOfAllWinners == null || mapOfAllWinners.isEmpty()) {
                    System.out.println("Alle haben verloren");
                    mapOfAllWinners.put("Alle", "haben verloren");
                }
                if (connected)
                    gameGraphics.showGameResults(mapOfAllWinners);
                gameHasEnded = true;
                gameEnded();
            }
            case POKER -> {
                if (dealer.getDealerHand() == null || dealer.getDealerHand().isEmpty()) {
                    for (int i = 0; i < 3; i++) {
                        rotateStepperMotor(13);
                        numberOfCardFaceup = 5;
                        executeCameraScan();
                        Thread.sleep(100);
                        executeCardThrow();
                        giveDealerNextCard();
                        checkForOldCard();
                        if (connected) {
                            gameGraphics.addCardToTable(dealer.getDealerHand().get(i));
                        }
                        System.out.println("Jetzt Karte entnehmen");
                        Thread.sleep(3000);
                    }
                } else {
                    currenPlayerIndex = 0;
                    rotateStepperMotor(13);
                    executeCameraScan();
                    executeCardThrow();
                    giveDealerNextCard();
                    checkForOldCard();
                    if (connected) {
                        gameGraphics.addCardToTable(dealer.getDealerHand().get(dealer.getDealerHand().size() - 1));
                    }
                    if (dealer.getDealerHand().size() >= 5) {
                        Thread.sleep(10000);
                        if (connected)
                            gameGraphics.showGameResults(mapOfAllWinners);
                        gameHasEnded = true;
                        gameEnded();
                    }
                }
            }
        }
    }

    public static void executeCardThrow() throws InterruptedException {
        cardMotor.werfeKarteAus();
        System.out.println("Karte wurde gewurfen");
    }

    private static void giveCurrentPlayerNextCard() {
        if (currentPlayer.getKartenhand() == null) {
            currentPlayer.setKartenhand(new ArrayList<Karte>());
            currentPlayer.getKartenhand().add(nextCardInDeck);
        } else {
            currentPlayer.getKartenhand().add(nextCardInDeck);
        }
    }

    private static void executeCameraScan() {
//        nextCardInDeck = new Karte("10", "Herz", "Herz 10");
        for (int i = 0; i < 10; i++) {
            BufferedImage bufferedImage = camera.captureImage();

            if (bufferedImage != null) {
                try {
                    String decodedText = camera.decodeQRCode(bufferedImage);
                    if (decodedText != null) {
                        System.out.println("Decoded text: " + decodedText);
                        Karte karte = mapper.readValue(decodedText, Karte.class);
                        oldCard = karte;
                        nextCardInDeck = karte;
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

    private static void giveDealerNextCard() {
        if (dealer.getDealerHand() == null) {
            dealer.setDealerHand(new ArrayList<Karte>());
            dealer.getDealerHand().add(nextCardInDeck);
        } else {
            dealer.getDealerHand().add(nextCardInDeck);
        }
    }

    public static void checkForOldCard() throws InterruptedException {
        Karte karte = new Karte();

        for (int i = 0; i < 10; i++) {
            BufferedImage bufferedImage = camera.captureImage();

            if (bufferedImage != null) {
                try {
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
        while (oldCard.equals(karte)) {
            executeCardThrow();
            BufferedImage bufferedImage = camera.captureImage();

            if (bufferedImage != null) {
                try {
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

    private static void initializeGame() throws InterruptedException, TMCDeviceIsBusyException {
        for(Player player : listOfAllPlayers){
            System.out.println("Spieler " + player.getId() +" bei der Initialisierung.");
        }
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
                    if (connected) {
                        if (i == 0)
                            gameGraphics.addCardToTable(dealer.getDealerHand().get(0));
                        else
                            gameGraphics.addBeginningCards();
                    }
                    System.out.println("Jetzt Karte entnehmen");
                    Thread.sleep(3000);
                }
                System.out.println("Jetzt sijnnd die Spieler dran");
                for (Player player : listOfAllPlayers) {
                    currentPlayer = player;
                    for (int i = 0; i < 2; i++) {
                        rotateStepperMotor(Integer.parseInt(currentPlayer.getId()));
                        executeCameraScan();
                        Thread.sleep(100);
                        executeCardThrow();
                        giveCurrentPlayerNextCard();
                        checkForOldCard();
                        System.out.println("Jetzt Karte entnehmen");
                        Thread.sleep(3000);
                    }
                }
                buttonClickedOnce = false;
                buttonClickedTwice = false;
                checkForPlayerHands();
            }
            case POKER -> {
                Raspberry_Controller.isGameModePoker = true;
                for (Player player : listOfAllPlayers) {
                    currentPlayer = player;
                    for (int i = 0; i < 2; i++) {
                        rotateStepperMotor(Integer.parseInt(currentPlayer.getId()));
                        executeCameraScan();
                        Thread.sleep(100);
                        executeCardThrow();
                        giveCurrentPlayerNextCard();
                        checkForOldCard();
                        System.out.println("Jetzt Karte entnehmen");
                        Thread.sleep(3000);
                    }
                }
                currentPlayer = new Player("0");
                buttonClickedOnce = false;
                buttonClickedTwice = false;
            }
        }
        System.out.println("Karten wurden ausgeteilt");
    }

    private static void checkForPlayerHands() throws InterruptedException, TMCDeviceIsBusyException {
        ArrayList<Player> listOfToRemovingPlayer = new ArrayList<>();
        for (Player player : listOfAllPlayers) {
            player.countHand();
            if (player.getKartenhandWert() == 21) {
                listOfToRemovingPlayer.add(player);
                System.out.println("Spieler wurde hinzugefügt");
            }
        }
        if (!listOfToRemovingPlayer.isEmpty()) {
            for (Player player : listOfToRemovingPlayer) {
                listOfAllPlayers.remove(player);
                blackJackList.add(player);
                System.out.println("Spieler wurde entfernt");
                someOneStartedWithBlackJack = true;
            }
        }
        if (listOfAllPlayers != null && !listOfAllPlayers.isEmpty()) {
            if (listOfAllPlayers.size() >= 2) {
                Collections.sort(listOfAllPlayers, new Comparator<Player>() {
                    @Override
                    public int compare(Player p1, Player p2) {
                        return Integer.compare(Integer.parseInt(p1.getId()), Integer.parseInt(p2.getId()));
                    }
                });
            }
            currentPlayer = listOfAllPlayers.get(0);
        } else {
            System.out.println("es wurde gechekt");
            executeComputerTurn();
        }
    }

    private static void restartTheCurrentgame() {
        gameRestarted = false;
        oldCard = new Karte();
        someOneStartedWithBlackJack = false;
        gameChoiceReseted = false;
        blackJackList = new ArrayList<>();
        listOfAllPlayers = (ArrayList<Player>) listOfAllPlayersWhoPlayTheGame.clone();
        for (Player player : listOfAllPlayerAtTheBeginningOfTheGame) {
            System.out.println(player.getId());
        }
        dealer.resetDealer();
        for (Player player : listOfAllPlayers) {
            player.resetPlayer();
        }

        turnHasEnded = false;
        restartTheCurrentGame();
    }

    public boolean isGameChoiceReseted() {
        return gameChoiceReseted;
    }

    public void setGameChoiceReseted(boolean gameChoiceReseted) {
        this.gameChoiceReseted = gameChoiceReseted;
    }

    public boolean isGameRestarted() {
        return gameRestarted;
    }

    public void setGameRestarted(boolean gameRestarted) {
        this.gameRestarted = gameRestarted;
    }

    public void buttonClicked() {
        this.buttonClickedOnce = true;
    }

    public boolean isButtonClickedOnce() {
        return buttonClickedOnce;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public String getGetMessage() {
        return getMessage;
    }

    public void setGetMessage(String getMessage) {
        this.getMessage = getMessage;
    }

    public void setButtonClickedOnce(boolean buttonClickedOnce) {
        this.buttonClickedOnce = buttonClickedOnce;
    }

    public boolean isGameReset() {
        return gameReset;
    }

    public void setGameReset(boolean gameReset) {
        this.gameReset = gameReset;
    }

    public int getPlayerAtReset() {
        return playerAtReset;
    }

    public void setPlayerAtReset(int playerAtReset) {
        this.playerAtReset = playerAtReset;
    }

    public int getPlayerGotReseted() {
        return playerGotReseted;
    }

    public void setPlayerGotReseted(int playerGotReseted) {
        this.playerGotReseted = playerGotReseted;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public ArrayList<Player> getListOfAllPlayers() {
        return listOfAllPlayers;
    }

    public void setListOfAllPlayers(ArrayList<Player> listOfAllPlayers) {
        this.listOfAllPlayers = listOfAllPlayers;
    }

    public boolean isButtonClickedTwice() {
        return buttonClickedTwice;
    }

    public void setButtonClickedTwice(boolean buttonClickedTwice) {
        this.buttonClickedTwice = buttonClickedTwice;
    }

    public int getCurrenPlayerIndex() {
        return currenPlayerIndex;
    }

    public void setCurrenPlayerIndex(int currenPlayerIndex) {
        this.currenPlayerIndex = currenPlayerIndex;
    }

    public Karte getNextCardInDeck() {
        return nextCardInDeck;
    }

    public void setNextCardInDeck(Karte nextCardInDeck) {
        this.nextCardInDeck = nextCardInDeck;
    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public void setGamemode(Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    public int getNumberOfCardFaceup() {
        return numberOfCardFaceup;
    }

    public void setNumberOfCardFaceup(int numberOfCardFaceup) {
        this.numberOfCardFaceup = numberOfCardFaceup;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Computer getDealer() {
        return dealer;
    }

    public void setDealer(Computer dealer) {
        this.dealer = dealer;
    }

    public HashMap<String, String> getMapOfAllWinners() {
        return mapOfAllWinners;
    }

    public void setMapOfAllWinners(HashMap<String, String> mapOfAllWinners) {
        this.mapOfAllWinners = mapOfAllWinners;
    }

    public static void restartTheCurrentGame() {
        adminPanelRotateStepper = new Pair<>(false, 0);

        adminPanelCardThrowActivated = false;

        mapOfAllWinners = new HashMap<>();

        numberOfCardFaceup = 1;

        buttonClickedOnce = false;

        buttonClickedTwice = false;

        getMessage = "";

        nextCardInDeck = null;

        currentPlayer = new Player();

        currenPlayerIndex = 0;

        dealer = new Computer();
    }

    public static void resetGameChoice() {
        adminPanelRotateStepper = new Pair<>(false, 0);

        adminPanelCardThrowActivated = false;

        mapOfAllWinners = new HashMap<>();

        gamemode = Gamemode.BLACKJACK;

        connected = true;

        numberOfCardFaceup = 1;

        buttonClickedOnce = false;

        buttonClickedTwice = false;

        getMessage = "";

        gameStarted = false;

        gameHasEnded = false;

        Karte nextCardInDeck = null;

        currentPlayer = new Player();

        listOfAllPlayers = new ArrayList<>();

        currenPlayerIndex = 0;

        dealer = new Computer();
    }

    public boolean isGameHasEnded() {
        return gameHasEnded;
    }

    public void setGameHasEnded(boolean gameHasEnded) {
        this.gameHasEnded = gameHasEnded;
    }

    public Pair<Boolean, Integer> getAdminPanelRotateStepper() {
        return adminPanelRotateStepper;
    }

    public void setAdminPanelRotateStepper(Pair<Boolean, Integer> adminPanelRotateStepper) {
        this.adminPanelRotateStepper = adminPanelRotateStepper;
    }

    public boolean isAdminPanelCardThrowActivated() {
        return adminPanelCardThrowActivated;
    }

    public void setAdminPanelCardThrowActivated(boolean adminPanelCardThrowActivated) {
        this.adminPanelCardThrowActivated = adminPanelCardThrowActivated;
    }
}
