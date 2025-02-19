package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

@SpringBootApplication
public class Web_Client {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Web_Client.class, args);
        GameService gameService = context.getBean(GameService.class);
//        while(true){
//            System.out.println(gameService.isButtonClicked());
//        }
          System.out.println(gameService.isButtonClicked());
    }
}