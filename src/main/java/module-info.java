module org.example.blackjackfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.blackjackfx to javafx.fxml;
    exports org.example.blackjackfx;
}