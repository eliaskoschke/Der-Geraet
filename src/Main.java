import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    public static void main(String[] args) {
        // Erstelle ein JFrame-Fenster
        JFrame frame = new JFrame("Einfaches Fenster");

        // Setze die Größe des Fensters
        frame.setSize(400, 300);

        // Beende das Programm, wenn das Fenster geschlossen wird
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Erstelle eine Schaltfläche
        JButton button = new JButton("Klick mich!");
        JButton button2 = new JButton("Klick mich!");

        // Erstelle eine Instanz von Buttonvariablen für jeden Button
        Buttonvariablen buttonVar1 = new Buttonvariablen();
        Buttonvariablen buttonVar2 = new Buttonvariablen();

        // Füge einen ActionListener hinzu, um auf Klicks zu reagieren
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.currentTimeMillis();

                if ((currentTime - buttonVar1.lastPressTime) < buttonVar1.DOUBLE_CLICK_TIME) {
                    buttonVar1.doubleKlick = true;
                } else {
                    buttonVar1.einzelKlick = true;
                    new Thread(buttonVar1::checkeDoubleKlick).start();
                }
                buttonVar1.lastPressTime = currentTime;
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.currentTimeMillis();

                if ((currentTime - buttonVar2.lastPressTime) < buttonVar2.DOUBLE_CLICK_TIME) {
                    buttonVar2.doubleKlick = true;
                } else {
                    buttonVar2.einzelKlick = true;
                    new Thread(buttonVar2::checkeDoubleKlick).start();
                }
                buttonVar2.lastPressTime = currentTime;
            }
        });

        // Füge die Schaltfläche zum JFrame hinzu
        frame.add(button);
        frame.add(button2);

        // Setze das Layout des JFrame
        frame.setLayout(null);

        // Setze die Position und Größe der Schaltfläche
        button.setBounds(50, 100, 100, 50);
        button2.setBounds(150, 100, 100, 50);

        // Mache das Fenster sichtbar
        frame.setVisible(true);
    }
}