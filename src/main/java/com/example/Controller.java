package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {
    ArrayList<String> playersAtTable = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();
    private String getMessage = "";
    private boolean gameHasStarted = false;
    boolean gameReset = false;
    int playerAtReset = 0;
    int playerGotReseted = 0;
    String currentPlayer = "";
    ArrayList<String> dealerHand = (ArrayList<String>) List.of("{\"wert\": \"3\", \"typ\": \"Pik\", \"name\": \"Pik 3\"}",
            "{\"wert\": \"4\", \"typ\": \"Pik\", \"name\": \"Pik 4\"}",
            "{\"wert\": \"5\", \"typ\": \"Pik\", \"name\": \"Pik 5\"}",
            "{\"wert\": \"6\", \"typ\": \"Pik\", \"name\": \"Pik 6\"}");

    public Controller() {
    }

    @GetMapping("/onload")
    public ResponseMessage receiveMessageSitzplatz() throws JsonProcessingException {
        System.out.println(playersAtTable);
        return new ResponseMessage(mapper.writeValueAsString(playersAtTable));
    }

    @GetMapping("/isConnected")
    public ResponseMessage isConnected() throws JsonProcessingException {
        return new ResponseMessage("false");
    }

    @PostMapping("/user/playerJoinedTheTable")
    public ResponseMessage playerJoinedTheTable(@RequestBody Message message) {
        System.out.println("Nachricht erhalten: " + message.getMessage());
        if (!playersAtTable.contains(message.getMessage())) {
            playersAtTable.add(message.getMessage());
            return new ResponseMessage("acknowledged");
        }
        return new ResponseMessage("not acknowledged");
    }

    @PostMapping("/user/playerLeftTheTable")
    public ResponseMessage playerLeftTheTable(@RequestBody Message message) {
        System.out.println("Nachricht erhalten: " + message.getMessage());
        if (playersAtTable.contains(message.getMessage())) {
            playersAtTable.remove(message.getMessage());
            System.out.println("Es sollte aus der Liste sein " + playersAtTable);
        }
        return null;
    }

    @GetMapping("/user/hasGameStarted")
    public ResponseMessage hasGameSatrted() throws JsonProcessingException {
        if (gameHasStarted) {
            return new ResponseMessage("true");
        }
        return new ResponseMessage(mapper.writeValueAsString("false"));
    }

    @GetMapping("/user/hasGameReseted")
    public ResponseMessage hasGameReseted() throws JsonProcessingException {
        if (gameReset) {
            playerGotReseted += 1;
            if (playerGotReseted >= playerAtReset) {
                playerAtReset = 0;
                playerGotReseted = 0;
                gameReset = false;
            }
            return new ResponseMessage("true");
        }
        return new ResponseMessage(mapper.writeValueAsString("false"));
    }

    @GetMapping("/user/ping")
    public ResponseMessage userPing() throws JsonProcessingException {
        if (gameHasStarted) {
            return new ResponseMessage("Game has started");
        }
        if (gameReset) {
            playerGotReseted += 1;
            if (playerGotReseted >= playerAtReset) {
                playerAtReset = 0;
                playerGotReseted = 0;
                gameReset = false;
            }
            return new ResponseMessage("Game was reseted");
        }
        return new ResponseMessage(mapper.writeValueAsString("Nothing happened"));
    }

    @GetMapping("/admin/ping")
    public ResponseMessage adminPing() throws JsonProcessingException {
        return new ResponseMessage(String.valueOf(playersAtTable.size()));
    }

    @PostMapping("/admin/sendPassword")
    public ResponseMessage sendPassword(@RequestBody Message postPassword) {
        if (postPassword.getMessage().equals("1111")) {
            return new ResponseMessage("true");
        }
        return new ResponseMessage("false");
    }

    @PostMapping("/admin/startGame")
    public ResponseMessage startGame(@RequestBody Message postPassword) {
        gameHasStarted = true;
        return new ResponseMessage("true");
    }

    @PostMapping("/admin/sendReset")
    public ResponseMessage sendReset(@RequestBody Message postPassword) {
        gameReset = true;
        playerAtReset = playersAtTable.size();
        playersAtTable = new ArrayList<>();
        return new ResponseMessage("true");
    }

    @GetMapping("/game/ping/getDealerHand")
    public ResponseMessage getDealerHand() throws JsonProcessingException {
        return new ResponseMessage(String.valueOf(dealerHand));
    }

    @GetMapping("/game/ping/getPlayerTurn")
    public ResponseMessage getPlayerTurn() throws JsonProcessingException {
        return new ResponseMessage(currentPlayer);
    }


    public static class Message {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}