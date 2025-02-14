public class Buttonvariablen {
    boolean doubleKlick = false;
    boolean einzelKlick = false;
    int DOUBLE_CLICK_TIME = 400;

    long lastPressTime = 0;

    public void checkeDoubleKlick() {
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
