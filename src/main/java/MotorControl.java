
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;

public class MotorControl {

    public static void main(String[] args) throws InterruptedException {
        // Pi4J-Kontext initialisieren
        Context pi4j = Pi4J.newAutoContext();

        // GPIO-Pins für IN1 und IN2 konfigurieren
        int in1PinNumber = 17; // Beispiel-Pin-Nummer für IN1
        int in2PinNumber = 18; // Beispiel-Pin-Nummer für IN2

        DigitalOutput in1 = pi4j.dout().create(DigitalOutput.newConfigBuilder(pi4j)
                .id("IN1")
                .name("Motor IN1")
                .address(in1PinNumber)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output"));

        DigitalOutput in2 = pi4j.dout().create(DigitalOutput.newConfigBuilder(pi4j)
                .id("IN2")
                .name("Motor IN2")
                .address(in2PinNumber)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output"));

        // Motor vorwärts drehen
        in1.high();
        in2.low();
        System.out.println("Motor dreht vorwärts");
        Thread.sleep(2000);

        // Motor rückwärts drehen
        in1.low();
        in2.high();
        System.out.println("Motor dreht rückwärts");
        Thread.sleep(2000);

        // Motor stoppen
        in1.low();
        in2.low();
        System.out.println("Motor gestoppt");

        // Pi4J-Kontext beenden
        pi4j.shutdown();
    }
}



