package com.example;

import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;

import javax.naming.Context;
import java.util.HashMap;

public class MappingForButtonIds {

    //        addButtonOutputs("1", 20);
    //        addButtonOutputs("2", 16);
    //        addButtonOutputs("3", 12);
    //        addButtonOutputs("4", 7);
    //        addButtonOutputs("5", 8);
    //        addButtonOutputs("6", 25);

    public static int getLEDPinAdressForPlayerID(String playerID){
        return switch (playerID) {
            case "1" -> 20;
            case "2" -> 16;
            case "3" -> 12;
            case "4" -> 7;
            case "5" -> 8;
            case "6" -> 25;
            default -> 0;
        };
    }

    //        buttonList.add(new PiButton(pi4j, 26));
    //        buttonList.add(new PiButton(pi4j, 24));
    //        buttonList.add(new PiButton(pi4j, 4));
    //        buttonList.add(new PiButton(pi4j, 14));
    //        buttonList.add(new PiButton(pi4j, 17));
    //        buttonList.add(new PiButton(pi4j, 27));

    public static int getButtonPinAdressForPlayerID(String playerID){
        return switch (playerID) {
            case "1" -> 26;
            case "2" -> 24;
            case "3" -> 4;
            case "4" -> 14;
            case "5" -> 17;
            case "6" -> 27;
            default -> 0;
        };
    }
}
