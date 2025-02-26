package com.example;

public class MappingForAdress {

    //        addButtonOutputs("1", 20);
    //        addButtonOutputs("2", 16);
    //        addButtonOutputs("3", 12);
    //        addButtonOutputs("4", 7);
    //        addButtonOutputs("5", 8);
    //        addButtonOutputs("6", 25);
    public static int getLEDPinAdressForPlayerID(String playerID){
        return switch (playerID) {
            case "1" -> 6;
            case "2" -> 7;
            case "3" -> 8;
            case "4" -> 9;
            case "5" -> 10;
            case "6" -> 11;
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
            case "1" -> 16;
            case "2" -> 17;
            case "3" -> 22;
            case "4" -> 25;
            case "5" -> 26;
            case "6" -> 27;
            default -> 0;
        };
    }


    //        int in1PinNumber = 12;
    //        int in2PinNumber = 5;
    public static int getMotorAdress(String motorID){
        return switch ((motorID)) {
            case "1" -> 12;
            case "2" -> 5;
            default -> 0;
        };
    }

    public static int getConnectionAdress(){
        return 13;
    }


}
