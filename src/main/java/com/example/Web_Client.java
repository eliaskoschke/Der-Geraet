package com.example;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

@SpringBootApplication
public class Web_Client {
    static com. pi4j. context. Context pi4j =  Pi4J.newAutoContext();
    static DigitalOutput stepperMotor;
    static DigitalOutput discardMotor;
    static DigitalOutput camera;
    public static void main(String[] args) throws InterruptedException {
        controllerConfig();
        ApplicationContext context = SpringApplication.run(Web_Client.class, args);
        GameService gameService = context.getBean(GameService.class);
        System.out.println(gameService.isButtonClickedOnce());
        while(true){
            if(gameService.isButtonClickedOnce()){
                hitEvent();
                gameService.setButtonClickedOnce(false);
            }
            if(gameService.isButtonClickedTwice()){
                stayEvent();
                gameService.setButtonClickedTwice(false);
            }
            Thread.sleep(100);
        }

    }

    public static void hitEvent(){
        stepperMotor.high();
    }
    public static void stayEvent(){
        stepperMotor.low();
    }

    public static void controllerConfig(){
        var stepperMotorConfig = DigitalOutput.newConfigBuilder(pi4j)
                .name("Stepper Motor")
                .id("Stepper Motor ID")
                .address(14) //passende adresse einfügen
                .initial(DigitalState.LOW)
                .onState(DigitalState.HIGH);
        stepperMotor = pi4j.create(stepperMotorConfig);
    }
}