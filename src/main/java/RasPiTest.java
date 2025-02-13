import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

public class RasPiTest {
    public static void main(String[] args) throws InterruptedException {
        var pi4j = Pi4J.newAutoContext();

        var config = DigitalInput.newConfigBuilder(pi4j)
                .id("Button1")
                .name("Button Test1")
                .address(27)
                .pull(PullResistance.PULL_DOWN)
                .debounce(1000L);

        var config2 = DigitalInput.newConfigBuilder(pi4j)
                .id("Button2")
                .name("Button Test2")
                .address(22)
                .pull(PullResistance.PULL_DOWN)
                .debounce(1000L);

        var button = pi4j.create(config);
        var button2 = pi4j.create(config2);

        button.addListener(e -> {
            if (e.state() == DigitalState.HIGH) {
                System.out.println("Knopf 1 gedrückt");
            }
        });
        button2.addListener(e -> {
            if (e.state() == DigitalState.HIGH) {
                System.out.println("Knopf 2 gedrückt");
            }
        });

        Thread.sleep(60000);

        pi4j.shutdown();
    }
}
