package com.pi4j.example;

import com.pi4j.Pi4J;
import com.pi4j.example.tmc2209.TMCCommunicationException;
import com.pi4j.example.tmc2209.TMCDeviceIsBusyException;
import com.pi4j.example.tmc2209.Tmc2209;

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
        steppermotor.moveToPosition(calc(grad),SPEED);
    }

    private int calc(int grad) {
        int result = (int) (grad * 2.6 * 4);
        return result;
    }
}
