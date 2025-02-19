package com.example;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    private boolean buttonClicked = false;

    private String getMessage = "";

    private boolean gameStarted = false;

    boolean gameReset = false;

    int playerAtReset = 0;

    int playerGotReseted = 0;

    ArrayList<Karte> dealerHand = new ArrayList<>();

    ArrayList<String> dealerHandBildId = new ArrayList<>();

    private Player currentPlayer = new Player();

    private ArrayList<Player> listOfAllPlayers = new ArrayList<>();

    public void buttonClicked() {
        this.buttonClicked = true;
    }

    public boolean isButtonClicked() {
        return buttonClicked;
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

    public void setButtonClicked(boolean buttonClicked) {
        this.buttonClicked = buttonClicked;
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

    public ArrayList<Karte> getDealerHand() {
        return dealerHand;
    }

    public void setDealerHand(ArrayList<Karte> dealerHand) {
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
}
