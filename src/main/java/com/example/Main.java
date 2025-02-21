package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;


import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;



import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

@SpringBootApplication
public class Main {
    static com. pi4j. context. Context pi4j =  Pi4J.newAutoContext();
    static DigitalOutput stepperMotor;
    static DigitalOutput discardMotorIn1;
    static DigitalOutput discardMotorIn2;
    static DigitalOutput cameraOutput;
    static GameService gameService;
    static Camera camera = new Camera();
    static ObjectMapper mapper = new ObjectMapper();
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
        executeCameraScan();
        executeCardThrow();
    }

    private static void executeCameraScan(){
        BufferedImage bufferedImage=camera.captureImage();

        //camera.displayImage(bufferedImage);

        if (bufferedImage != null) {
            try {
                // Dekodiere den QR-Code
                String decodedText = camera.decodeQRCode(bufferedImage);
                if (decodedText != null) {
                    System.out.println("Decoded text: " + decodedText);
                    Karte karte = mapper.readValue(decodedText, Karte.class);
                    gameService.setNextCardInDeck(karte);
                    System.out.println(gameService.getNextCardInDeck().getName());
                } else {
                    System.out.println("QR-Code nicht gefunden");
                }
            } catch (Exception e) {
                System.out.println("Fehler beim Dekodieren des QR-Codes: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Fehler: Kein Bild von der Webcam erhalten.");
        }

    }



    private static void executeCardThrow() throws InterruptedException {
        discardMotorIn1.low();
        discardMotorIn2.high();
        System.out.println("Motor wurde angesteuert");
        Thread.sleep(1000);
        discardMotorIn1.low();
        discardMotorIn2.low();
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

        int in1PinNumber = 22; // Beispiel-Pin-Nummer für IN1
        int in2PinNumber = 27; // Beispiel-Pin-Nummer für IN2

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