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

    @GetMapping("/test")
    public String hello() {
        return getMessage;
    }

    @PostMapping("/user/test")
    public String helloPost(@RequestBody Message postMessage) {
        getMessage = postMessage.getMessage();
        System.out.println("message bekommen" + postMessage.getMessage());
        return "Post bekommen";
    }

    @GetMapping("/onload")
    public ResponseMessage receiveMessageSitzplatz() throws JsonProcessingException {
        System.out.println("Get request bekommen");
        System.out.println(playersAtTable);
        return new ResponseMessage(mapper.writeValueAsString(playersAtTable));
    }

    @GetMapping("/user/onload")
    public ResponseMessage hasGameSatrted() throws JsonProcessingException {
        System.out.println("Get request bekommen");
        System.out.println(playersAtTable);
        return new ResponseMessage(mapper.writeValueAsString(playersAtTable));
    }

    @PostMapping("/admin/sendPassword")
    public ResponseMessage sendPassword(@RequestBody Message postPassword) {
        if(postPassword.getMessage().equals("1111")){
            return new ResponseMessage("true");
        }
        return new ResponseMessage("false");
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