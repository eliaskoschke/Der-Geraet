package com.pi4j.example;

import com.pi4j.Pi4J;
import com.pi4j.example.tmc2209.TMCCommunicationException;
import com.pi4j.example.tmc2209.TMCDeviceIsBusyException;

public class MainZumTest {
    public static void main(String[] args) throws TMCCommunicationException, InterruptedException, TMCDeviceIsBusyException {
        var pi4j = Pi4J.newAutoContext();
        StepperController stepper = new StepperController(pi4j);


        System.out.println("0.");
        stepper.turn(0);
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("1.");
        stepper.turn(3);
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("2.");
        stepper.turn(180);
        try{
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        pi4j.shutdown();
    }
}
