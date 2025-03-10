package com.example;

public class KartenMotorFake implements IKartenMotor{

    @Override
    public void werfeKarteAus() {
        System.out.println("Karte wird ausgeworfen");
    }
}
