package com.pi4j.example;

import com.pi4j.Pi4J;
import com.pi4j.example.tmc2209.TMCCommunicationException;
import com.pi4j.example.tmc2209.TMCDeviceIsBusyException;
import com.pi4j.example.tmc2209.Tmc2209;

import java.util.Scanner;

public class SepperController {
    private static final int PIN_INDEX = 23;
    private static final int PIN_STEP = 18;
    private static final int PIN_DIR = 24;
    private static final int SPEED = 2000;
    Tmc2209 steppermotor;

    public SepperController() throws TMCCommunicationException, InterruptedException {
        var pi4j = Pi4J.newAutoContext();
        steppermotor = new Tmc2209(pi4j, PIN_INDEX, PIN_DIR, PIN_STEP);
    }

    void turn(int grad) throws TMCDeviceIsBusyException {
       // steppermotor.moveToPosition(calc(grad),SPEED);
        System.out.println(calc(grad));
    }

    private int calc(int grad) {
        int result = (int) (grad /1.8 * 2.6 * 8);
        return result;
    }

    public static void main(String[] args) throws TMCCommunicationException, InterruptedException, TMCDeviceIsBusyException {
        Scanner x = new Scanner(System.in);

        SepperController motor = new SepperController();

        while (true) {
            motor.turn(x.nextInt());
        }
    }
}
