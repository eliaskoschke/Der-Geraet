//package com.example;
//
//import com.pi4j.Pi4J;
//import com.pi4j.context.Context;
//import com.pi4j.io.gpio.digital.DigitalOutput;
//import com.pi4j.io.gpio.digital.DigitalState;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class PiButtonTester {
//
//    static HashMap<String, DigitalOutput> playerButtonMap = new HashMap<>();
//    static Context pi4j =  Pi4J.newAutoContext();
//    public static void main(String[] args) throws InterruptedException {
//        ArrayList<PiButton> buttonList = new ArrayList<>();
//        buttonList.add(new PiButton(pi4j, 26));
//        buttonList.add(new PiButton(pi4j, 24));
//        buttonList.add(new PiButton(pi4j, 4));
//        buttonList.add(new PiButton(pi4j, 14));
//        buttonList.add(new PiButton(pi4j, 17));
//        buttonList.add(new PiButton(pi4j, 27));
//        controllerConfig();
//        while(true){
////            for(PiButton piButton : buttonList){
////                piButton.checkSingleClick();
////            }
//        }
//    }
//
//    public static void controllerConfig(){
//
//        addButtonOutputs("1", 20);
//        addButtonOutputs("2", 16);
//        addButtonOutputs("3", 12);
//        addButtonOutputs("4", 7);
//        addButtonOutputs("5", 8);
//        addButtonOutputs("6", 25);
//
//    }
//
//    private static void addButtonOutputs(String playerID, int adresse) {
//        var buttonOutput = pi4j.create(DigitalOutput.newConfigBuilder(pi4j)
//                .name("Freigabe für Player "+ playerID)
//                .id("Freigabe "+String.valueOf(adresse))
//                .address(adresse) //passende adresse einfügen
//                .initial(DigitalState.HIGH)
//                .onState(DigitalState.HIGH));
//
//        playerButtonMap.put(playerID, buttonOutput);
//    }
//}
