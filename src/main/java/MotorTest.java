import com.pi4j.Pi4J;

public class MotorTest {
    public static void main(String[] args) throws InterruptedException {
        var pi4j = Pi4J.newAutoContext();
        MotorController motor = new MotorController(pi4j);
        for (int i = 0; i < 10; i++) {
            motor.werfeSpielerKatreAus();
            Thread.sleep(500);
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        pi4j.shutdown();
    }
}
