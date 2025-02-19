package com.example;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.analog.AnalogOutputConfig;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.gpio.digital.impl.DefaultDigitalOutputConfigBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.naming.Context;

import java.util.concurrent.TimeUnit;

import static com.pi4j.Pi4J.newAutoContext;

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
        System.out.println(gameService.isButtonClicked());
        while(true){
            if(gameService.isButtonClicked()){
                rotateMachine(1);
                gameService.setButtonClicked(false);
            }
            Thread.sleep(100);
        }

    }

    public static void rotateMachine(int angle){
        stepperMotor.blink(1, TimeUnit.SECONDS);
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