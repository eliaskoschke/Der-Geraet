module Der.Geraet.Maven {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.boot.autoconfigure;
    requires com.pi4j;
    requires com.fasterxml.jackson.databind;
    requires spring.boot;
    requires spring.context;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.bytedeco.javacv;
    requires java.desktop;
    requires com.google.zxing.javase;
    requires com.google.zxing;
    requires spring.beans;
    requires spring.web;
    requires org.bytedeco.opencv;
    requires com.fazecast.jSerialComm;
    requires spring.data.jpa;
    requires jakarta.persistence;
    requires spring.orm;
    requires spring.tx;
    requires org.hibernate.orm.core;
    requires jakarta.xml.bind;

    opens com.example to javafx.graphics, spring.beans, spring.context, org.hibernate.orm.core;
    exports com.example;
}