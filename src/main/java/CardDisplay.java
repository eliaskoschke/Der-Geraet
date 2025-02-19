import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CardDisplay extends JPanel {
    private BufferedImage[] cardImages = new BufferedImage[52];
    private static final int CARD_WIDTH = 100;  // Breite der Spielkarte
    private static final int CARD_HEIGHT = 150; // Höhe der Spielkarte

    public CardDisplay() {
        loadCardImages();
    }

    private void loadCardImages() {
        for (int i = 0; i < 52; i++) {
            try {
                cardImages[i] = ImageIO.read(new File("CardFront/card" + i + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < cardImages.length; i++) {
            BufferedImage scaledImage = getScaledImage(cardImages[i], CARD_WIDTH, CARD_HEIGHT);
            int x = (getWidth() - CARD_WIDTH) / 2;
            int y = (getHeight() - CARD_HEIGHT) / 2 + i * (CARD_HEIGHT + 10);
            g2d.drawImage(scaledImage, x, y, this);
        }
    }

    private BufferedImage getScaledImage(BufferedImage src, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, width, height, null);
        g2.dispose();
        return resized;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Card Display");
        CardDisplay panel = new CardDisplay();
        frame.add(panel);
        frame.setSize(400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
