package org.example.blackjackfx;

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


public class QRCodeScannerPi {

    public static void main(String[] args) throws FrameGrabber.Exception {
        BufferedImage bufferedImage = captureImage();

        displayImage(bufferedImage);

        if (bufferedImage != null) {
            try {
                // Dekodiere den QR-Code
                String decodedText = decodeQRCode(bufferedImage);
                if (decodedText != null) {
                    System.out.println("Decoded text: " + decodedText);
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
}