import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.pwm.Pwm;

public class MotorController {
    private final Context pi4j;

    private final int DIRECTIONPIN1 = 5;
    private final int DIRECTIONPIN2 = 21;
    private final int PWMPIN = 12;
    private final int FREQUENCY = 200;

    private DigitalOutput direction1;
    private DigitalOutput direction2;

    private Pwm pwm;

    public MotorController(Context pi4j){
        this.pi4j = pi4j;

        direction1 = pi4j.dout().create(DIRECTIONPIN1);
        direction2 = pi4j.dout().create(DIRECTIONPIN2);

        pwm = pi4j.pwm().create(Pwm.newConfigBuilder(pi4j)
                .id("PWM")
                .name("Motorgeschwindigkeit")
                .address(PWMPIN)
                .initial(0)
                .shutdown(0)
                .provider("pigpio-pwm")
                .build());
    }

    public void werfeSpielerKatreAus(){
        dreheVorwaerts(100,100);
        dreheRueckwerts(75,300);
    }

    public void werfeDealerKatreAus(){
        dreheVorwaerts(75,150);
        dreheRueckwerts(76,301);
    }

    private void dreheRueckwerts(int speed, int dauer){
        try{
            direction1.high();
            direction2.low();
            pwm.on(speed,FREQUENCY);
            Thread.sleep(dauer);
            direction1.low();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally{
            pwm.off();
        }
        
    }

    private void dreheVorwaerts(int speed, int dauer){
        try{
            direction1.low();
            direction2.high();
            pwm.on(speed,FREQUENCY);
            Thread.sleep(dauer);
            direction2.low();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally{
            pwm.off();
        }
    }
}
