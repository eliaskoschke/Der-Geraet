package com.pi4j.example;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.example.tmc2209.TMCCommunicationException;
import com.pi4j.example.tmc2209.TMCDeviceIsBusyException;
import com.pi4j.example.tmc2209.Tmc2209;

public class StepperController {
    private static final int PIN_INDEX = 23;
    private static final int PIN_STEP = 18;
    private static final int PIN_DIR = 24;
    private static final int SPEED = 500;
    private static final int TRESHOLD = 35; //TODO: Rausfinden, wieviel ein Treshold haben sollte
    Tmc2209 steppermotor;

    Context pi4j;

    public StepperController(Context pi4j) throws TMCCommunicationException, InterruptedException {
        this.pi4j = pi4j;
        steppermotor = new Tmc2209(pi4j, PIN_INDEX, PIN_DIR, PIN_STEP);
    }

    public void orientieren() throws TMCDeviceIsBusyException {
        steppermotor.homePosition(SPEED,TRESHOLD);
    }

    void turn(int grad) throws TMCDeviceIsBusyException {
        steppermotor.moveToPosition(calc(grad),SPEED);
    }

    private int calc(int grad) {
        int result = (int) ((grad * 8) /(1.8 / 2.6 ));
        return result;
    }
}
