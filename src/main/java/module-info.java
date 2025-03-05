module org.example.fxgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.pi4j;

    opens org.example.fxgui to javafx.fxml;
    exports org.example.fxgui;
}