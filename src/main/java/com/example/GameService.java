package com.example;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    private Gamemode gamemode = Gamemode.BLACKJACK;

    private boolean connected = false;

    private int numberOfCardFaceup = 1;

    private boolean buttonClickedOnce = false;

    private boolean buttonClickedTwice = false;

    private String getMessage = "";

    private boolean gameStarted = false;

    boolean gameReset = false;

    Karte nextCardInDeck;

    int playerAtReset = 0;

    int playerGotReseted = 0;

    List<Karte> dealerHand ;

    ArrayList<String> dealerHandBildId = new ArrayList<>();

    private Player currentPlayer = new Player();

    private ArrayList<Player> listOfAllPlayers = new ArrayList<>();

    private int currenPlayerIndex = 0;

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

    public List<Karte> getDealerHand() {
        return dealerHand;
    }

    public void setDealerHand(List<Karte> dealerHand) {
        this.dealerHand = dealerHand;
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

    public ArrayList<String> getDealerHandBildId() {
        return dealerHandBildId;
    }

    public void setDealerHandBildId(ArrayList<String> dealerHandBildId) {
        this.dealerHandBildId = dealerHandBildId;
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
}
