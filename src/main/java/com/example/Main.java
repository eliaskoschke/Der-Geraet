package com.example;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

@SpringBootApplication
public class Main {
    static com. pi4j. context. Context pi4j =  Pi4J.newAutoContext();
    static DigitalOutput stepperMotor;
    static DigitalOutput discardMotorIn1;
    static DigitalOutput discardMotorIn2;
    static DigitalOutput camera;
    static GameService gameService;
    public static void main(String[] args) throws InterruptedException {
        controllerConfig();
        ApplicationContext context = SpringApplication.run(Main.class, args);
        gameService = context.getBean(GameService.class);
        gameLogic();
    }

    private static void gameLogic() throws InterruptedException {
        boolean actionHasBeenTaken = false;
        while(true){
            if(gameService.isButtonClickedOnce()){
                hitEvent();
                gameService.setButtonClickedOnce(false);
            }
            if(gameService.isButtonClickedTwice()){
                stayEvent();
                gameService.setButtonClickedTwice(false);
            }
            if (actionHasBeenTaken) {
                executeNextTurn();
                actionHasBeenTaken = false;
            }
            Thread.sleep(100);
        }
    }

    public static void executeNextTurn(){
        if(gameService.getCurrenPlayerIndex()+1 >= gameService.getListOfAllPlayers().size()){
            executeComputerTurn();
        } else{
            gameService.setCurrenPlayerIndex(gameService.getCurrenPlayerIndex()+1);
        }
    }

    public static void executeComputerTurn(){
        rotateStepperMotor(60);
        System.out.println("Test");
        gameService.setCurrenPlayerIndex(0);
    }

    public static void hitEvent() throws InterruptedException {
        discardMotorIn1.high();
        discardMotorIn2.low();
        Thread.sleep(100);
        discardMotorIn1.low();
        discardMotorIn2.high();
    }
    public static void stayEvent() throws InterruptedException {
        stepperMotor.high();
        Thread.sleep(100);
        stepperMotor.low();
        stepperMotor.low();
    }
    public static void rotateStepperMotor(int angle){
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

        int in1PinNumber = 17; // Beispiel-Pin-Nummer für IN1
        int in2PinNumber = 18; // Beispiel-Pin-Nummer für IN2

        discardMotorIn1 = pi4j.dout().create(DigitalOutput.newConfigBuilder(pi4j)
                .id("IN1")
                .name("Motor IN1")
                .address(in1PinNumber)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output"));

        discardMotorIn2 = pi4j.dout().create(DigitalOutput.newConfigBuilder(pi4j)
                .id("IN2")
                .name("Motor IN2")
                .address(in2PinNumber)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output"));
    }
}