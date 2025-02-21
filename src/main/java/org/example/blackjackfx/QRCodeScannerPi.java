package org.example.blackjackfx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import java.awt.image.BufferedImage;


public class QRCodeScannerPi {

    public static String scan() throws NotFoundException, InterruptedException {
        BufferedImage image;
        int counter = 0;

        while (counter <= 10) {
            image = captureImage();
            counter++;
            if (image != null) {
                String decodedText = decodeQRCode(image);
                if (decodedText != null && !decodedText.isEmpty()) {
                    System.out.println(decodedText);
                    return decodedText;
                } else {
                    System.out.println("QR-Code nicht gefunden");
                }
            }
        }
        return "";
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

    private static String decodeQRCode(BufferedImage bufferedImage) throws NotFoundException {
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }
}