import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QRCodeScanner {

    public static void main(String[] args) {
        // Lade das Bild von den Ressourcen
        String filePath = "/2025-02-11 14_38_39-.jpg"; // Beachte das führende Slash
        InputStream inputStream = QRCodeScanner.class.getResourceAsStream(filePath);

        if (inputStream == null) {
            System.out.println("Fehler: Die Datei konnte nicht gefunden werden.");
            return;
        }

        try {
            // Lade das Bild
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            if (bufferedImage == null) {
                System.out.println("Fehler: Das Bild konnte nicht geladen werden. Überprüfe den Dateipfad und das Dateiformat.");
                return;
            }

            // Dekodiere den QR-Code
            String decodedText = decodeQRCode(bufferedImage);
            if (decodedText != null) {
                System.out.println("Decoded text: " + decodedText);
                storeInDatabase(decodedText, "DeinZugewiesenerWert", "Dein Typ", "Dein Name");
                

            } else {
                System.out.println("QR-Code nicht gefunden");
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen des Bildes: " + e.getMessage());
            e.printStackTrace();
        } catch (NotFoundException e) {
            System.out.println("QR-Code nicht gefunden");
        }
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