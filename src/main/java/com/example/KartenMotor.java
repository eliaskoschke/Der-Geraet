package com.example;

import com.example.tcm2209.StepperController;
import com.example.tcm2209.TMCDeviceIsBusyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.pwm.Pwm;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class KartenMotor implements IKartenMotor{
    private DigitalOutput direction1;
    private DigitalOutput direction2;
    private Pwm pwm;
    private final int FREQUENCY = 200;
    private int counter = 1;
    private int sleep = 225;
    private static int forwardTimer = 90;
    private static int forwardPower = 100;
    private static int backwardTimer = 400;
    private static int backwardPower = 60;


    public KartenMotor(Context pi4j){
         direction1 = pi4j.dout().create(MappingForAdress.getMotorAdress("1"));
         direction2 = pi4j.dout().create(MappingForAdress.getMotorAdress("2"));

         pwm = pi4j.pwm().create(Pwm.newConfigBuilder(pi4j)
                 .id("PWM")
                 .name("PWM Name")
                 .address(MappingForAdress.getMotorAdress("3"))
                 .initial(0)
                 .shutdown(0)
                 .provider("pigpio-pwm")
                 .build());
    }

    public void werfeKarteAus() throws InterruptedException {
        dreheVorwaerts(forwardPower, forwardTimer);
        if(counter>8 && counter < 25)
            counter = 8;
        if(counter>= 25)
            counter = 25;
        forwardTimer++;
        if(forwardTimer>110)
            forwardTimer = 110;
        Thread.sleep(sleep/counter);
        counter++;
        dreheRueckwaerts(backwardPower,backwardTimer);
//        int time = 90 * (1 + (4/(52-counter +1)));
//        int speed = (int) ( 100* (0.95 * (1- (0.3 / (52-counter +1)))));
//        dreheVorwaerts(100, 105);
//        Thread.sleep((long) (sleep/1.75));
//        counter++;
//
//        if(counter>20)
//            forwardTimer=100;
//        dreheRueckwaerts(55, 550);

    }

    public void readData() {
        forwardPower = DatenbankController.getValueByName("vorwaertsGeschwindigkeit");
        forwardTimer = DatenbankController.getValueByName("vorwaertsDauer");
        backwardPower = DatenbankController.getValueByName("rueckwaertsGeschwindigkeit");
        backwardTimer = DatenbankController.getValueByName("rueckwaertsDauer");
    }

    private void dreheRueckwaerts(int speed, int dauer){
        try{
            direction1.high();
            direction2.low();
            pwm.on(speed, FREQUENCY);
            Thread.sleep(dauer);
            direction1.low();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void dreheVorwaerts(int speed, int dauer){
        try{
            direction1.low();
            direction2.high();
            pwm.on(speed, FREQUENCY);
            Thread.sleep(dauer);
            direction2.low();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
