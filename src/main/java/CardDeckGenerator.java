

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

    public class CardDeckGenerator {


        private static final int CARD_WIDTH = 822;
        private static final int CARD_HEIGHT = 1122;
        private static final int QR_CODE_SIZE = 150;
        private static final String BACKGROUND_IMAGE_PATH = "D:\\code\\Programme\\Kamera\\src\\main\\resources\\logo5.png";

        public static void main(String[] args) {
            String[] cards = {
                    "{\"wert\": \"2\", \"typ\": \"Pik\", \"name\": \"Pik 2\"}",
                    "{\"wert\": \"3\", \"typ\": \"Pik\", \"name\": \"Pik 3\"}",
                    "{\"wert\": \"4\", \"typ\": \"Pik\", \"name\": \"Pik 4\"}",
                    "{\"wert\": \"5\", \"typ\": \"Pik\", \"name\": \"Pik 5\"}",
                    "{\"wert\": \"6\", \"typ\": \"Pik\", \"name\": \"Pik 6\"}",
                    "{\"wert\": \"7\", \"typ\": \"Pik\", \"name\": \"Pik 7\"}",
                    "{\"wert\": \"8\", \"typ\": \"Pik\", \"name\": \"Pik 8\"}",
                    "{\"wert\": \"9\", \"typ\": \"Pik\", \"name\": \"Pik 9\"}",
                    "{\"wert\": \"10\", \"typ\": \"Pik\", \"name\": \"Pik 10\"}",
                    "{\"wert\": \"10\", \"typ\": \"Pik\", \"name\": \"Pik Bube\"}",
                    "{\"wert\": \"10\", \"typ\": \"Pik\", \"name\": \"Pik Dame\"}",
                    "{\"wert\": \"10\", \"typ\": \"Pik\", \"name\": \"Pik König\"}",
                    "{\"wert\": \"11\", \"typ\": \"Pik\", \"name\": \"Pik Ass\"}",

                    "{\"wert\": \"2\", \"typ\": \"Herz\", \"name\": \"Herz 2\"}",
                    "{\"wert\": \"3\", \"typ\": \"Herz\", \"name\": \"Herz 3\"}",
                    "{\"wert\": \"4\", \"typ\": \"Herz\", \"name\": \"Herz 4\"}",
                    "{\"wert\": \"5\", \"typ\": \"Herz\", \"name\": \"Herz 5\"}",
                    "{\"wert\": \"6\", \"typ\": \"Herz\", \"name\": \"Herz 6\"}",
                    "{\"wert\": \"7\", \"typ\": \"Herz\", \"name\": \"Herz 7\"}",
                    "{\"wert\": \"8\", \"typ\": \"Herz\", \"name\": \"Herz 8\"}",
                    "{\"wert\": \"9\", \"typ\": \"Herz\", \"name\": \"Herz 9\"}",
                    "{\"wert\": \"10\", \"typ\": \"Herz\", \"name\": \"Herz 10\"}",
                    "{\"wert\": \"10\", \"typ\": \"Herz\", \"name\": \"Herz Bube\"}",
                    "{\"wert\": \"10\", \"typ\": \"Herz\", \"name\": \"Herz Dame\"}",
                    "{\"wert\": \"10\", \"typ\": \"Herz\", \"name\": \"Herz König\"}",
                    "{\"wert\": \"11\", \"typ\": \"Herz\", \"name\": \"Herz Ass\"}",

                    "{\"wert\": \"2\", \"typ\": \"Karo\", \"name\": \"Karo 2\"}",
                    "{\"wert\": \"3\", \"typ\": \"Karo\", \"name\": \"Karo 3\"}",
                    "{\"wert\": \"4\", \"typ\": \"Karo\", \"name\": \"Karo 4\"}",
                    "{\"wert\": \"5\", \"typ\": \"Karo\", \"name\": \"Karo 5\"}",
                    "{\"wert\": \"6\", \"typ\": \"Karo\", \"name\": \"Karo 6\"}",
                    "{\"wert\": \"7\", \"typ\": \"Karo\", \"name\": \"Karo 7\"}",
                    "{\"wert\": \"8\", \"typ\": \"Karo\", \"name\": \"Karo 8\"}",
                    "{\"wert\": \"9\", \"typ\": \"Karo\", \"name\": \"Karo 9\"}",
                    "{\"wert\": \"10\", \"typ\": \"Karo\", \"name\": \"Karo 10\"}",
                    "{\"wert\": \"10\", \"typ\": \"Karo\", \"name\": \"Karo Bube\"}",
                    "{\"wert\": \"10\", \"typ\": \"Karo\", \"name\": \"Karo Dame\"}",
                    "{\"wert\": \"10\", \"typ\": \"Karo\", \"name\": \"Karo König\"}",
                    "{\"wert\": \"11\", \"typ\": \"Karo\", \"name\": \"Karo Ass\"}",

                    "{\"wert\": \"2\", \"typ\": \"Kreuz\", \"name\": \"Kreuz 2\"}",
                    "{\"wert\": \"3\", \"typ\": \"Kreuz\", \"name\": \"Kreuz 3\"}",
                    "{\"wert\": \"4\", \"typ\": \"Kreuz\", \"name\": \"Kreuz 4\"}",
                    "{\"wert\": \"5\", \"typ\": \"Kreuz\", \"name\": \"Kreuz 5\"}",
                    "{\"wert\": \"6\", \"typ\": \"Kreuz\", \"name\": \"Kreuz 6\"}",
                    "{\"wert\": \"7\", \"typ\": \"Kreuz\", \"name\": \"Kreuz 7\"}",
                    "{\"wert\": \"8\", \"typ\": \"Kreuz\", \"name\": \"Kreuz 8\"}",
                    "{\"wert\": \"9\", \"typ\": \"Kreuz\", \"name\": \"Kreuz 9\"}",
                    "{\"wert\": \"10\", \"typ\": \"Kreuz\", \"name\": \"Kreuz 10\"}",
                    "{\"wert\": \"10\", \"typ\": \"Kreuz\", \"name\": \"Kreuz Bube\"}",
                    "{\"wert\": \"10\", \"typ\": \"Kreuz\", \"name\": \"Kreuz Dame\"}",
                    "{\"wert\": \"10\", \"typ\": \"Kreuz\", \"name\": \"Kreuz König\"}",
                    "{\"wert\": \"11\", \"typ\": \"Kreuz\", \"name\": \"Kreuz Ass\"}"
            };

            for (int i = 0; i < cards.length; i++) {
                String cardJson = cards[i];
                try {
                    // Den Namen der Karte aus dem JSON-String extrahieren
                    String cardName = cardJson.split("\"name\": \"")[1].split("\"")[0];
                    BufferedImage cardImage = createCardImage(cardJson);
                    File outputfile = new File(cardName + ".png");
                    ImageIO.write(cardImage, "png", outputfile);
                } catch (IOException | WriterException e) {
                    e.printStackTrace();
                }
            }
        }

        private static BufferedImage createCardImage(String cardJson) throws IOException, WriterException {
            BufferedImage cardImage = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = cardImage.createGraphics();

            // Hintergrundbild laden und zeichnen
            BufferedImage backgroundImage = ImageIO.read(new File(BACKGROUND_IMAGE_PATH));
            g.drawImage(backgroundImage, 0, 0, CARD_WIDTH, CARD_HEIGHT, null);

            // QR-Code erzeugen
            BufferedImage qrCode = generateQRCodeImage(cardJson);

            // QR-Code oben rechts zeichnen
            g.drawImage(qrCode, CARD_WIDTH - QR_CODE_SIZE - 100, 100, QR_CODE_SIZE, QR_CODE_SIZE, null);

            // QR-Code unten links zeichnen
            g.drawImage(qrCode, 100, CARD_HEIGHT - QR_CODE_SIZE - 100, QR_CODE_SIZE, QR_CODE_SIZE, null);

            g.dispose();
            return cardImage;
        }

//        private static BufferedImage generateQRCodeImage(String text) throws WriterException {
//            QRCodeWriter qrCodeWriter = new QRCodeWriter();
//            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
//
//            BufferedImage qrCodeImage = new BufferedImage(QR_CODE_SIZE, QR_CODE_SIZE, BufferedImage.TYPE_INT_RGB);
//            for (int x = 0; x < QR_CODE_SIZE; x++) {
//                for (int y = 0; y < QR_CODE_SIZE; y++) {
//                    int rgb = bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB();
//                    qrCodeImage.setRGB(x, y, rgb);
//                }
//            }
//            return qrCodeImage;
//        }

        private static BufferedImage generateQRCodeImage(String text) throws WriterException {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int qrWidth = width;
            int qrHeight = height;

            // Zuschnitt des QR-Codes, um den weißen Rand zu entfernen
            int[] enclosingRectangle = bitMatrix.getEnclosingRectangle();
            if (enclosingRectangle != null) {
                qrWidth = enclosingRectangle[2];
                qrHeight = enclosingRectangle[3];
            }

            BufferedImage qrCodeImage = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < qrWidth; x++) {
                for (int y = 0; y < qrHeight; y++) {
                    int rgb = bitMatrix.get(x + enclosingRectangle[0], y + enclosingRectangle[1]) ? Color.BLACK.getRGB() : Color.WHITE.getRGB();
                    qrCodeImage.setRGB(x, y, rgb);
                }
            }
            return qrCodeImage;
        }

    }

