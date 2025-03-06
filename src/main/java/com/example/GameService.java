package com.example;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class GameService {
    //Todo: static weg. WARUM?????

    private static HashMap<String, String> mapOfAllWinners = new HashMap<>();

    private static Gamemode gamemode = Gamemode.BLACKJACK;

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


    public void restartTheCurrentGame(){
        mapOfAllWinners = new HashMap<>();

        numberOfCardFaceup = 1;

        buttonClickedOnce = false;

        buttonClickedTwice = false;

        getMessage = "";

        nextCardInDeck = null;

        playerAtReset = 0;

        playerGotReseted = 0;

        currentPlayer = new Player();

        currenPlayerIndex = 0;

        dealer = new Computer();
    }

    public void resetGameChoice(){
        mapOfAllWinners = new HashMap<>();

        gamemode = Gamemode.BLACKJACK;

        connected = false;

        numberOfCardFaceup = 1;

        buttonClickedOnce = false;

        buttonClickedTwice = false;

        getMessage = "";

        gameStarted = false;

        gameReset = false;

        gameHasEnded = false;

        Karte nextCardInDeck = null;

        playerAtReset = 0;

        playerGotReseted = 0;

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
}
