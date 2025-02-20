import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;


import java.awt.image.BufferedImage;

public class CardDispenser {

    public static void main(String[] args) throws Exception {
        // Pi4J-Kontext initialisieren
        Context pi4j = Pi4J.newAutoContext();

        // GPIO-Pins für IN1 und IN2 konfigurieren
        int in1PinNumber = 17;
        int in2PinNumber = 18;

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

        // QR-Code scannen
        boolean qrCodeScanned = scanQRCode();

        if (qrCodeScanned) {
            // Motor vorwärts drehen
            in1.high();
            in2.low();
            System.out.println("Motor dreht vorwärts");
            Thread.sleep(2000); // Geschwindigkeit anpassen

            // Motor rückwärts drehen
            in1.low();
            in2.high();
            System.out.println("Motor dreht rückwärts");
            Thread.sleep(500); // Rückzugszeit anpassen

            // Motor stoppen
            in1.low();
            in2.low();
            System.out.println("Motor gestoppt");
        }

        // Pi4J-Kontext beenden
        pi4j.shutdown();
    }

    // Methode zum Scannen eines QR-Codes
    public static boolean scanQRCode() {
        BufferedImage bufferedImage = captureImage();
        if (bufferedImage != null) {
            try {
                String decodedText = decodeQRCode(bufferedImage);
                if (decodedText != null) {
                    System.out.println("Decoded text: " + decodedText);
                    return true; // QR-Code erfolgreich gescannt
                } else {
                    System.out.println("QR-Code nicht gefunden");
                }
            } catch (Exception e) {
                System.out.println("Fehler beim Dekodieren des QR-Codes: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Fehler: Kein Bild von der Webcam erhalten.");
        }
        return false; // QR-Code nicht gescannt
    }

    public static BufferedImage captureImage() {
        try {
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
            grabber.start();
            Frame frame = grabber.grab();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bufferedImage = converter.convert(frame);
            grabber.stop();
            return bufferedImage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decodeQRCode(BufferedImage bufferedImage) throws NotFoundException {
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }
}
