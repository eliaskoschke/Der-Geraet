package org.example.fxgui;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

import java.util.ArrayList;
import java.util.List;

import static com.pi4j.Pi4J.newAutoContext;

public class PiController {
    static Context pi4j = newAutoContext();

    DigitalInput button1;
    DigitalInput button2;

    static ArrayList<DigitalInput> buttons = new ArrayList<>();

    public PiController() {
        DigitalInputConfigBuilder buttonConfig1 = DigitalInput.newConfigBuilder(pi4j)
                .name("Button")
                .id("1")
                .address(27)
                .debounce(1000L)
                .pull(PullResistance.PULL_DOWN);

        DigitalInputConfigBuilder buttonConfig2 = DigitalInput.newConfigBuilder(pi4j)
                .name("Button 2")
                .id("2")
                .address(22)
                .debounce(1000L)
                .pull(PullResistance.PULL_DOWN);

        button1 = pi4j.create(buttonConfig1);
        button2 = pi4j.create(buttonConfig2);

        buttons.add(button1);
        buttons.add(button2);

        for (DigitalInput button : buttons) {
            button.addListener(e -> {
               if (button.state().equals(DigitalState.HIGH)){
                   updateSeats(MainWindow.seats, button);
               }
            });
        }
    }

    public static void updateSeats(List<Boolean> list, DigitalInput button) {
        if (list.get(Integer.parseInt(button.id()) - 1) && button.state().equals(DigitalState.HIGH)) {
            list.set(Integer.parseInt(button.id()) - 1, false);
        } else if (!list.get(Integer.parseInt(button.id()) - 1) && button.state().equals(DigitalState.HIGH)) {
            list.set(Integer.parseInt(button.id()) - 1, true);
        }
    }
}
