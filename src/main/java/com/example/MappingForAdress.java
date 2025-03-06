package com.example;

public class MappingForAdress {
    //TODO: Exception wefen Statt "0" als Adresse
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

    public static int getMotorAdress(String motorID){
        return switch (motorID) {
            case "1" -> 12;
            case "2" -> 5;
            default -> 0;
        };
    }

    public static int getConnectionAdress(){
        return 13;
    }
}
