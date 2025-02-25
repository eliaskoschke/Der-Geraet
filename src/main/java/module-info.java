module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires org.bytedeco.javacv;
    requires com.pi4j;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires spring.beans;
    requires spring.web;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires java.naming;
    requires jakarta.ws.rs;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    opens com.example to javafx.fxml;
    exports com.example;

}