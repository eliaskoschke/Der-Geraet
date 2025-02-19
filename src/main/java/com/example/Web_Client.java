package com.example;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
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
    static com.pi4j.context.Context pi4j = (com.pi4j.context.Context) newAutoContext();
    static com. pi4j. io. gpio. digital. DigitalOutput stepperMotor;
    static com. pi4j. io. gpio. digital. DigitalOutput discardMotor;
    static com. pi4j. io. gpio. digital. DigitalOutput camera;
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = SpringApplication.run(Web_Client.class, args);
        GameService gameService = context.getBean(GameService.class);
        System.out.println(gameService.isButtonClicked());
        while(true){
            if(gameService.isButtonClicked()){
                rotateMachine(1);
            }
            Thread.sleep(100);
        }

    }

    public static void rotateMachine(int angle){
        stepperMotor.blink(1, TimeUnit.SECONDS);
    }
    public static void controllerConfig(){
        stepperMotor = (DigitalOutput) DigitalOutput.newConfigBuilder(pi4j)
                .name("Stepper Motor")
                .id("Stepper Motor ID")
                .address(1) //passende adresse einfügen
                .initial(DigitalState.LOW);

    }
}