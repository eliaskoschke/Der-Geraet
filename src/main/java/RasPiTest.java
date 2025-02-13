import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.util.Console;

public class RasPiTest {
    public static void main(String[] args) {
        final  Console console = new Console();

        var pi4j = Pi4J.newAutoContext();

        var config = DigitalInput.newConfigBuilder(pi4j)
                .id("Button")
                .name("Button")
                .address(2)
                .pull(PullResistance.PULL_DOWN)
                .build();

        var button = pi4j.create(config);

        button.addListener(e -> {
           if (e.state() == DigitalState.HIGH) {
               console.println("Knopf gedrückt");
           } else {
               console.println("Knopf losgelassen");
           }
        });

        console.promptForExit();
        pi4j.shutdown();
    }
}