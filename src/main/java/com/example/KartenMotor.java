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
public class KartenMotor {
    private Context pi4j;
    private DigitalOutput direction1;
    private DigitalOutput direction2;
    private Pwm pwm;
    private final int FREQUENCY = 200;

    public KartenMotor(Context pi4j){
        this.pi4j = pi4j;
         direction1 = pi4j.dout().create(MappingForAdress.getMotorAdress("1"));
         direction1 = pi4j.dout().create(MappingForAdress.getMotorAdress("2"));

         pwm = pi4j.pwm().create(Pwm.newConfigBuilder(pi4j)
                 .id("PWM")
                 .name("PWM Name")
                 .address(MappingForAdress.getMotorAdress("3"))
                 .initial(0)
                 .shutdown(0)
                 .provider("pigpio-pwm")
                 .build());
    }

    public void werfeKarteAus(){
        dreheVorwaerts(65, 120);
        dreheRueckwaerts(100, 100);
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
