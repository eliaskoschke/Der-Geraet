package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class Controller {
    ArrayList<String> playersAtTable = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();
    private String getMessage ="";
    private boolean gameHasStarted = false;
    boolean gameReset = false;
    int playerAtReset =0;
    int playerGotReseted = 0;
    public Controller() {
    }

    @PostMapping("/user/playerJoinedTheTable")
    public ResponseMessage playerJoinedTheTable(@RequestBody Message message) {
        System.out.println("Nachricht erhalten: " + message.getMessage());
        if(!playersAtTable.contains(message.getMessage())){
            playersAtTable.add(message.getMessage());
            System.out.println("Es sollte in der Liste sein "+ playersAtTable);
            return new ResponseMessage("acknowledged");
        }
        return new ResponseMessage("not acknowledged");
    }

    @PostMapping("/user/playerLeftTheTable")
    public ResponseMessage playerLeftTheTable(@RequestBody Message message) {
        System.out.println("Nachricht erhalten: " + message.getMessage());
        if(playersAtTable.contains(message.getMessage())){
            playersAtTable.remove(message.getMessage());
            System.out.println("Es sollte aus der Liste sein "+ playersAtTable);
        }
        return null;
    }

    @GetMapping("/onload")
    public ResponseMessage receiveMessageSitzplatz() throws JsonProcessingException {
        System.out.println("Get request bekommen");
        System.out.println(playersAtTable);
        return new ResponseMessage(mapper.writeValueAsString(playersAtTable));
    }

    @GetMapping("/user/hasGameStarted")
    public ResponseMessage hasGameSatrted() throws JsonProcessingException {
        if(gameHasStarted){
            return new ResponseMessage("true");
        }
        return new ResponseMessage(mapper.writeValueAsString("false"));
    }

    @GetMapping("/user/hasGameReseted")
    public ResponseMessage hasGameReseted() throws JsonProcessingException {
        if(gameReset){
            playerGotReseted += 1;
            if(playerGotReseted >= playerAtReset){
                playerAtReset =0;
                playerGotReseted = 0;
                gameReset = false;
            }
            return new ResponseMessage("true");
        }
        return new ResponseMessage(mapper.writeValueAsString("false"));
    }

    @GetMapping("/user/ping")
    public ResponseMessage userPing() throws JsonProcessingException {
        if(gameHasStarted){
            return new ResponseMessage("Game has started");
        }
        if(gameReset){
            playerGotReseted += 1;
            if(playerGotReseted >= playerAtReset){
                playerAtReset =0;
                playerGotReseted = 0;
                gameReset = false;
            }
            return new ResponseMessage("game was reseted");
        }
        return new ResponseMessage(mapper.writeValueAsString("nothing happened"));
    }

    @GetMapping("/admin/ping")
    public ResponseMessage adminPing() throws JsonProcessingException {
       return new ResponseMessage(String.valueOf(playersAtTable.size()));
    }

    @PostMapping("/admin/sendPassword")
    public ResponseMessage sendPassword(@RequestBody Message postPassword) {
        if(postPassword.getMessage().equals("1111")){
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