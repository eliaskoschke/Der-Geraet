package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {
    Socket serverSocket = new Socket("localhost", 1234);
    BufferedReader reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    PrintWriter writer = new PrintWriter(serverSocket.getOutputStream(), true);
    private ObjectMapper mapper = new ObjectMapper();
    public Controller() throws IOException {
    }

    @PostMapping("/message")
    public ResponseMessage receiveMessage(@RequestBody Message message) {
        System.out.println("Nachricht erhalten: " + message.getMessage());
        try{
            writer.println("load new Player: "+message.getMessage());
        }
        catch (Exception e){
            System.out.println(e);
        }
        System.out.println("Es hätte ausgegeben erden müssen");
        return null;
    }

    @PostMapping("/onload")
    public ResponseMessage receiveMessageSitzplatz(@RequestBody Message id) throws IOException {
        System.out.println("Nachricht erhalten: " + id.getMessage());
        writer.println("test");
        System.out.println("Es wurd geschickt");
        ArrayList response = mapper.readValue(reader.readLine(), ArrayList.class);
        System.out.println(response);
        return new ResponseMessage(mapper.writeValueAsString(response));
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