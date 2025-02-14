import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    private static final int DOUBLE_CLICK_TIME = 400;

    private static boolean doubleKlick = false;
    private static boolean einzelKlick = false;

    private static long lastPressTime = 0;

    public static void main(String[] args) {
        // Erstelle ein JFrame-Fenster
        JFrame frame = new JFrame("Einfaches Fenster");

        // Setze die Größe des Fensters
        frame.setSize(400, 300);

        // Beende das Programm, wenn das Fenster geschlossen wird
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Erstelle eine Schaltfläche
        JButton button = new JButton("Klick mich!");

        // Füge einen ActionListener hinzu, um auf Klicks zu reagieren
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.currentTimeMillis();

                if ((currentTime - lastPressTime) < DOUBLE_CLICK_TIME) {
                    doubleKlick = true;
                } else {
                    einzelKlick = true;
                    new Thread(Main::checkeDoubleKlick).start();
                }
                lastPressTime = currentTime;
            }
        });


        // Füge die Schaltfläche zum JFrame hinzu
        frame.add(button);

        // Setze das Layout des JFrame
        frame.setLayout(null);

        // Setze die Position und Größe der Schaltfläche
        button.setBounds(150, 100, 100, 50);

        // Mache das Fenster sichtbar
        frame.setVisible(true);
    }

    private static void checkeDoubleKlick() {
        if (einzelKlick) {
            try {
                Thread.sleep(DOUBLE_CLICK_TIME);
                if (doubleKlick) {
                    doubleKlick = false;
                    einzelKlick = false;
                    System.out.println("Doppel");
                } else {
                    einzelKlick = false;
                    System.out.println("Einzel");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}