import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;


import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;



import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class QRCodeScannerPi {

    public static void main(String[] args) throws FrameGrabber.Exception {

        // Bild von der Kamera laden
        BufferedImage bufferedImage=captureImage();

        displayImage(bufferedImage);


        if (bufferedImage != null) {
            try {
                // Dekodiere den QR-Code
                String decodedText = decodeQRCode(bufferedImage);
                if (decodedText != null) {
                    System.out.println("Decoded text: " + decodedText);
                    storeInDatabase(decodedText, "DeinZugewiesenerWert", "Dein Typ", "Dein Name");
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






//        String filePath = "/KarteMitCode.png"; // Beachte das führende Slash
//        InputStream inputStream = QRCodeScanner.class.getResourceAsStream(filePath);
//
//        if (inputStream == null) {
//            System.out.println("Fehler: Die Datei konnte nicht gefunden werden.");
//            return;
//        }
//        try {
//            // Lade das Bild
//            BufferedImage bufferedImage = ImageIO.read(inputStream);
//            if (bufferedImage == null) {
//                System.out.println("Fehler: Das Bild konnte nicht geladen werden. Überprüfe den Dateipfad und das Dateiformat.");
//                return;
//            }
//            // Dekodiere den QR-Code
//            String decodedText = decodeQRCode(bufferedImage);
//            if (decodedText != null) {
//                System.out.println("Decoded text: " + decodedText);
//                storeInDatabase(decodedText, "DeinZugewiesenerWert", "Dein Typ", "Dein Name");
//
//            } else {
//                System.out.println("QR-Code nicht gefunden");
//            }
//        } catch (IOException e) {
//            System.out.println("Fehler beim Lesen des Bildes: " + e.getMessage());
//            e.printStackTrace();
//        } catch (NotFoundException e) {
//            System.out.println("QR-Code nicht gefunden");
//        }
    }

    public static BufferedImage captureImage() {
        try {
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0); // 0 ist die Standard-Webcam
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

    private static void displayImage(BufferedImage img) {
        ImageIcon icon = new ImageIcon(img);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(img.getWidth(), img.getHeight());
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }



    private static String decodeQRCode(BufferedImage bufferedImage) throws NotFoundException {
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }

    private static void storeInDatabase(String code, String value, String typ, String name) {
        String url = "jdbc:sqlite:karte.db";
        String sql = "INSERT INTO Karten (code, value, typ, name) VALUES (?, ?, ?,?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, value);
            pstmt.setString(3, typ);
            pstmt.setString(4, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
