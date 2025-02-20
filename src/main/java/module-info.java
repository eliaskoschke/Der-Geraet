module org.example.blackjackfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires org.bytedeco.javacv;
    requires java.desktop;


    opens org.example.blackjackfx to javafx.fxml;
    exports org.example.blackjackfx;
}