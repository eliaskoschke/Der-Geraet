package org.example;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

public class TasterTesten {
    public static void main(String[] args) {
        Context pi4j = Pi4J.newAutoContext();

        var config2 = DigitalInput.newConfigBuilder(pi4j)
                .id("Button2")
                .name("Button Test2")
                .address(17)
                .pull(PullResistance.PULL_DOWN)
                .debounce(1000L);

        var config3 = DigitalInput.newConfigBuilder(pi4j)
                .id("Button3")
                .name("Button Test3")
                .address(22)
                .pull(PullResistance.PULL_DOWN)
                .debounce(1000L);

        var configout2 = DigitalOutput.newConfigBuilder(pi4j)
                .id("Button2")
                .name("Button Test2")
                .address(7)
                .onState(DigitalState.HIGH)
                .initial(DigitalState.LOW)
                .build();

        var configout3 = DigitalOutput.newConfigBuilder(pi4j)
                .id("Button3")
                .name("Button Test3")
                .address(8)
                .onState(DigitalState.HIGH)
                .initial(DigitalState.LOW)
                .build();

        var freigabe2 = pi4j.create(configout2);
        var freigabe3 = pi4j.create(configout3);

        var button2 = pi4j.create(config2);
        var button3 = pi4j.create(config3);

        freigabe2.low();
        freigabe3.low();

        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        freigabe2.high();
        freigabe3.high();




        button2.addListener(e -> {
            if (e.state() == DigitalState.HIGH) {
                System.out.println("Knopf 2 gedrückt");
            }
        });
        button3.addListener(e -> {
            if (e.state() == DigitalState.HIGH) {
                System.out.println("Knopf 3 gedrückt");
            }
        });



    }
}
