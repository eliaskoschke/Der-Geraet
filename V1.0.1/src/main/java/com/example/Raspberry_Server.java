package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Raspberry_Server {

    private static int port = 1234;
    private static ServerSocket serverSocket;
    private static ObjectMapper mapper = new ObjectMapper();
    public Raspberry_Server() throws IOException {
    }

    public static void main(String[]args) throws IOException {
        serverSocket = new ServerSocket(port);
        Socket clientSocketOne = serverSocket.accept();
        System.out.println("Client verbunden: " + clientSocketOne.getInetAddress());

        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocketOne.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocketOne.getOutputStream(), true);

        ArrayList<String> playerID = new ArrayList<>();

        Thread updatePlayerTableThread = new Thread(() -> {
            try {
                while (true) {
                    String message = reader.readLine();
                    if(message.contains("load new Player: ")){
                        System.out.println(message);
                        playerID.add(message.substring(message.indexOf(":")+2));
                    } else{
                        System.out.println("hlidshflsd");
                        writer.println(mapper.writeValueAsString(playerID));
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        updatePlayerTableThread.start();
    }


}
