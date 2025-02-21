package org.example.blackjackfx;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;

public class MotorController {
    Context pi4j = Pi4J.newAutoContext();

    DigitalOutputConfigBuilder config1= DigitalOutput.newConfigBuilder(pi4j)
            .name("Pin1")
            .id("1")
            .address(22)
            .initial(DigitalState.LOW);

    DigitalOutputConfigBuilder config2 = DigitalOutput.newConfigBuilder(pi4j)
            .name("Pin2")
            .id("2")
            .address(27)
            .initial(DigitalState.LOW);

    DigitalOutput pin1;
    DigitalOutput pin2;

    public MotorController() {
        pin1 = pi4j.create(config1);
        pin2 = pi4j.create(config2);
    }

    public void spin() throws InterruptedException {
        pin1.state(DigitalState.HIGH);
        pin2.state(DigitalState.LOW);
        Thread.sleep(60);

        pin1.state(DigitalState.LOW);
        pin2.state(DigitalState.HIGH);
        Thread.sleep(20);

        pin1.state(DigitalState.LOW);
        pin2.state(DigitalState.LOW);
    }
}
